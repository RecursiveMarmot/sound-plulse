package com.timess.soundpulse.strategy.impl;

import com.timess.soundpulse.constant.MediaTypeEnum;
import com.timess.soundpulse.model.dto.common.MediaInfoDTO;
import com.timess.soundpulse.strategy.BaseMediaStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AudioMediaStrategy extends BaseMediaStrategy {
    
    @Override
    public MediaTypeEnum getSupportedType() {
        return MediaTypeEnum.AUDIO;
    }
    
    @Override
    public boolean isSupported(String url) {
        MediaTypeEnum type = getMediaTypeFromUrl(url);
        return type == MediaTypeEnum.AUDIO;
    }
    
    @Override
    public boolean validate(File file) {
        // 基础验证：检查文件存在性和大小
        if (!file.exists() || file.length() == 0) {
            log.warn("音频文件无效: {}", file.getAbsolutePath());
            return false;
        }
        
        // 可以通过文件头魔数验证（Magic Number）
        boolean isValid = validateAudioMagicNumber(file);
        log.info("音频验证结果: {}", isValid);
        return isValid;
    }
    
    @Override
    public Map<String, Object> extractMetadata(File file) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            // 这里可以使用 jaudiotagger 等库提取详细信息
            metadata.put("duration", 0); // 时长（秒）
            metadata.put("bitrate", 0);  // 比特率
            metadata.put("sampleRate", 0); // 采样率
            metadata.put("format", getFileExtension(file));
            
            // 简单获取文件大小
            metadata.put("fileSize", file.length());
            
        } catch (Exception e) {
            log.error("提取音频元数据失败", e);
        }
        return metadata;
    }
    
    @Override
    protected String getFilePrefix() {
        return "audio_";
    }
    
    @Override
    protected String getDefaultExtension() {
        return "mp3";
    }
    
    private MediaTypeEnum getMediaTypeFromUrl(String url) {
        String lowerUrl = url.toLowerCase();
        for (String ext : MediaTypeEnum.AUDIO.getExtensions()) {
            if (lowerUrl.contains("." + ext)) {
                return MediaTypeEnum.AUDIO;
            }
        }
        
        try {
            MediaInfoDTO info = getMediaInfo(url);
            if (info != null) {
                return info.getMediaType();
            }
        } catch (Exception e) {
            log.warn("通过 HEAD 请求判断媒体类型失败: {}", url);
        }
        
        return null;
    }
    
    private boolean validateAudioMagicNumber(File file) {
        // 验证 MP3 文件头（ID3 标签）
        try {
            byte[] header = new byte[3];
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            fis.read(header);
            fis.close();
            
            // MP3 文件头标识: ID3
            if (header[0] == 'I' && header[1] == 'D' && header[2] == '3') {
                return true;
            }
            
            // 其他格式的验证...
            return true;
        } catch (Exception e) {
            log.warn("音频魔数验证失败", e);
            return false;
        }
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf(".");
        return lastDot > 0 ? name.substring(lastDot + 1) : "";
    }
}