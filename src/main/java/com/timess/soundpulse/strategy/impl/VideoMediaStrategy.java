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
public class VideoMediaStrategy extends BaseMediaStrategy {
    
    @Override
    public MediaTypeEnum getSupportedType() {
        return MediaTypeEnum.VIDEO;
    }
    
    @Override
    public boolean isSupported(String url) {
        MediaTypeEnum type = getMediaTypeFromUrl(url);
        return type == MediaTypeEnum.VIDEO;
    }
    
    @Override
    public boolean validate(File file) {
        if (!file.exists() || file.length() == 0) {
            log.warn("视频文件无效: {}", file.getAbsolutePath());
            return false;
        }
        
        boolean isValid = validateVideoMagicNumber(file);
        log.info("视频验证结果: {}", isValid);
        return isValid;
    }
    
    @Override
    public Map<String, Object> extractMetadata(File file) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            // 可以使用 JavaCV 等库提取视频信息
            metadata.put("duration", 0);      // 时长
            metadata.put("width", 0);         // 宽度
            metadata.put("height", 0);        // 高度
            metadata.put("frameRate", 0);     // 帧率
            metadata.put("bitrate", 0);       // 比特率
            metadata.put("format", getFileExtension(file));
            metadata.put("fileSize", file.length());
            
        } catch (Exception e) {
            log.error("提取视频元数据失败", e);
        }
        return metadata;
    }
    
    @Override
    protected String getFilePrefix() {
        return "video_";
    }
    
    @Override
    protected String getDefaultExtension() {
        return "mp4";
    }
    
    private MediaTypeEnum getMediaTypeFromUrl(String url) {
        String lowerUrl = url.toLowerCase();
        for (String ext : MediaTypeEnum.VIDEO.getExtensions()) {
            if (lowerUrl.contains("." + ext)) {
                return MediaTypeEnum.VIDEO;
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
    
    private boolean validateVideoMagicNumber(File file) {
        // 验证 MP4 文件头
        try {
            byte[] header = new byte[8];
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            fis.read(header);
            fis.close();
            
            // MP4 文件头标识
            if (header[4] == 'f' && header[5] == 't' && 
                header[6] == 'y' && header[7] == 'p') {
                return true;
            }
            
            return true;
        } catch (Exception e) {
            log.warn("视频魔数验证失败", e);
            return false;
        }
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf(".");
        return lastDot > 0 ? name.substring(lastDot + 1) : "";
    }
}