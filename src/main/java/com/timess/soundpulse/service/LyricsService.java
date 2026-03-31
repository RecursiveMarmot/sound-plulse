package com.timess.soundpulse.service;


import com.timess.soundpulse.model.dto.common.LyricsResponse;
import com.timess.soundpulse.model.dto.common.LyricsSearchRequest;

/**
 * 歌词服务接口
 */
public interface LyricsService {
    
    /**
     * 搜索歌词
     */
    LyricsResponse searchLyrics(LyricsSearchRequest request);
    
    /**
     * 根据ID获取歌词
     */
    LyricsResponse getLyricsById(Long lyricsId);
    
    /**
     * 批量获取歌词
     */
    LyricsResponse batchSearchLyrics(LyricsSearchRequest request);
}