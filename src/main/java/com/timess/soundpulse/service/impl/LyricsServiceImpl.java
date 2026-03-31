package com.timess.soundpulse.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.timess.soundpulse.model.dto.common.LyricLine;
import com.timess.soundpulse.model.dto.common.LyricsResponse;
import com.timess.soundpulse.model.dto.common.LyricsSearchRequest;
import com.timess.soundpulse.service.LyricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌词服务实现类
 * 使用 LRCLIB API: https://lrclib.net
 */
@Service
@Slf4j
public class LyricsServiceImpl implements LyricsService {
    
    private static final String LRCLIB_API_BASE = "https://lrclib.net/api";
    private static final String USER_AGENT = "SoundPulse/1.0 (https://soundpulse.com)";
    
    // LRC时间戳正则表达式: [00:12.34] 或 [00:12:34]
    private static final Pattern LRC_TIME_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})(?:\\.(\\d{2}))?\\]");
    
    /**
     * 搜索歌词
     */
    @Override
    @Cacheable(value = "lyrics", key = "#request.trackName + '_' + #request.artistName", unless = "#result.success == false")
    public LyricsResponse searchLyrics(LyricsSearchRequest request) {
        LyricsResponse response = new LyricsResponse();
        
        try {
            // 参数校验
            if (request == null || StrUtil.isBlank(request.getTrackName()) || StrUtil.isBlank(request.getArtistName())) {
                response.setSuccess(false);
                response.setMessage("歌曲名和歌手名不能为空");
                return response;
            }
            
            log.info("开始搜索歌词: 歌曲={}, 歌手={}", request.getTrackName(), request.getArtistName());
            
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("track_name", request.getTrackName());
            params.put("artist_name", request.getArtistName());
            
            if (StrUtil.isNotBlank(request.getAlbumName())) {
                params.put("album_name", request.getAlbumName());
            }
            
            if (request.getDuration() != null && request.getDuration() > 0) {
                params.put("duration", request.getDuration());
            }
            
            // 发送HTTP请求
            String responseBody = sendGetRequest(LRCLIB_API_BASE + "/search", params);
            
            if (StrUtil.isBlank(responseBody)) {
                response.setSuccess(false);
                response.setMessage("未找到歌词");
                return response;
            }
            
            // 解析响应
            JSONArray lyricsArray = JSONUtil.parseArray(responseBody);
            
            if (lyricsArray.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("未找到歌词");
                return response;
            }
            
            // 获取最佳匹配（取第一个）
            JSONObject firstMatch = lyricsArray.getJSONObject(0);
            
            // 构建返回数据
            LyricsResponse.LyricsData data = buildLyricsData(firstMatch);
            data.setTimeline(parseLrcToTimeline(data.getSyncedLyrics()));
            
            response.setSuccess(true);
            response.setMessage("获取歌词成功");
            response.setData(data);
            
            log.info("歌词获取成功: {} - {}", data.getArtistName(), data.getTrackName());
            
        } catch (Exception e) {
            log.error("搜索歌词异常", e);
            response.setSuccess(false);
            response.setMessage("获取歌词失败：" + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 根据ID获取歌词
     */
    @Override
    @Cacheable(value = "lyrics", key = "#lyricsId", unless = "#result.success == false")
    public LyricsResponse getLyricsById(Long lyricsId) {
        LyricsResponse response = new LyricsResponse();
        
        try {
            if (lyricsId == null || lyricsId <= 0) {
                response.setSuccess(false);
                response.setMessage("歌词ID无效");
                return response;
            }
            
            log.info("根据ID获取歌词: {}", lyricsId);
            
            String url = LRCLIB_API_BASE + "/get/" + lyricsId;
            String responseBody = sendGetRequest(url, null);
            
            if (StrUtil.isBlank(responseBody)) {
                response.setSuccess(false);
                response.setMessage("未找到歌词");
                return response;
            }
            
            JSONObject jsonObject = JSONUtil.parseObj(responseBody);
            LyricsResponse.LyricsData data = buildLyricsData(jsonObject);
            data.setTimeline(parseLrcToTimeline(data.getSyncedLyrics()));
            
            response.setSuccess(true);
            response.setMessage("获取歌词成功");
            response.setData(data);
            
        } catch (Exception e) {
            log.error("获取歌词详情异常", e);
            response.setSuccess(false);
            response.setMessage("获取歌词失败：" + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 批量搜索歌词（带重试机制）
     */
    @Override
    public LyricsResponse batchSearchLyrics(LyricsSearchRequest request) {
        LyricsResponse response = new LyricsResponse();
        
        try {
            // 先尝试精确搜索
            response = searchLyrics(request);
            
            // 如果精确搜索失败，尝试模糊搜索（移除部分关键词）
            if (!response.getSuccess() && StrUtil.isNotBlank(request.getTrackName())) {
                log.info("精确搜索失败，尝试模糊搜索...");
                
                String[] keywords = request.getTrackName().split("[\\s\\-()]");
                for (String keyword : keywords) {
                    if (keyword.length() > 3) {
                        LyricsSearchRequest fuzzyRequest = new LyricsSearchRequest();
                        fuzzyRequest.setTrackName(keyword);
                        fuzzyRequest.setArtistName(request.getArtistName());
                        
                        response = searchLyrics(fuzzyRequest);
                        if (response.getSuccess()) {
                            break;
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("批量搜索歌词异常", e);
            response.setSuccess(false);
            response.setMessage("搜索失败");
        }
        
        return response;
    }
    
    /**
     * 发送GET请求
     */
    private String sendGetRequest(String url, Map<String, Object> params) {
        try {
            HttpRequest request = HttpRequest.get(url)
                .header("User-Agent", USER_AGENT)
                .timeout(10000); // 10秒超时
            
            if (params != null && !params.isEmpty()) {
                request.form(params);
            }
            
            try (HttpResponse response = request.execute()) {
                if (response.isOk()) {
                    return response.body();
                } else {
                    log.error("HTTP请求失败: {} - {}", response.getStatus(), response.body());
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("发送HTTP请求异常: {}", url, e);
            return null;
        }
    }
    
    /**
     * 构建歌词数据对象
     */
    private LyricsResponse.LyricsData buildLyricsData(JSONObject json) {
        LyricsResponse.LyricsData data = new LyricsResponse.LyricsData();
        data.setLyricsId(json.getLong("id"));
        data.setTrackName(json.getStr("trackName"));
        data.setArtistName(json.getStr("artistName"));
        data.setAlbumName(json.getStr("albumName"));
        data.setPlainLyrics(json.getStr("plainLyrics"));
        data.setSyncedLyrics(json.getStr("syncedLyrics"));
        data.setDuration(json.getInt("duration"));
        data.setLanguage(json.getStr("language"));
        return data;
    }
    
    /**
     * 解析LRC格式歌词为时间轴对象
     * 输入格式：[00:12.34]歌词内容
     */
    public List<LyricLine> parseLrcToTimeline(String syncedLyrics) {
        List<LyricLine> timeline = new ArrayList<>();
        
        if (StrUtil.isBlank(syncedLyrics)) {
            return timeline;
        }
        
        String[] lines = syncedLyrics.split("\n");
        
        for (String line : lines) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            
            Matcher matcher = LRC_TIME_PATTERN.matcher(line);
            
            while (matcher.find()) {
                try {
                    // 解析时间戳
                    int minutes = Integer.parseInt(matcher.group(1));
                    int seconds = Integer.parseInt(matcher.group(2));
                    int millis = 0;
                    
                    if (matcher.group(3) != null) {
                        millis = Integer.parseInt(matcher.group(3));
                    }
                    
                    int totalMillis = (minutes * 60 + seconds) * 1000 + millis;
                    
                    // 提取歌词内容（去掉时间戳部分）
                    String content = line.substring(matcher.end()).trim();
                    
                    if (StrUtil.isNotBlank(content)) {
                        LyricLine lyricLine = new LyricLine();
                        lyricLine.setTime(totalMillis);
                        lyricLine.setContent(content);
                        lyricLine.setFormattedTime(String.format("%02d:%02d", minutes, seconds));
                        timeline.add(lyricLine);
                    }
                    
                } catch (Exception e) {
                    log.debug("解析LRC行失败: {}", line);
                }
            }
        }
        
        // 按时间排序
        timeline.sort((a, b) -> Integer.compare(a.getTime(), b.getTime()));
        
        log.debug("解析LRC完成，共{}行", timeline.size());
        return timeline;
    }
    
    /**
     * 获取歌词预览（前50个字符）
     */
    public String getLyricsPreview(String plainLyrics) {
        if (StrUtil.isBlank(plainLyrics)) {
            return "";
        }
        
        int length = Math.min(plainLyrics.length(), 100);
        String preview = plainLyrics.substring(0, length);
        
        if (plainLyrics.length() > length) {
            preview += "...";
        }
        
        return preview;
    }
}