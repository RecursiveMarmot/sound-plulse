package com.timess.soundpulse.strategy.impl;

import com.timess.soundpulse.constant.MediaTypeEnum;
import com.timess.soundpulse.model.dto.common.MediaInfoDTO;
import com.timess.soundpulse.strategy.BaseMediaStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ImageMediaStrategy extends BaseMediaStrategy {
    
    @Override
    public MediaTypeEnum getSupportedType() {
        return MediaTypeEnum.IMAGE;
    }
    
    @Override
    public boolean isSupported(String url) {
        MediaTypeEnum type = getMediaTypeFromUrl(url);
        return type == MediaTypeEnum.IMAGE;
    }
    
    @Override
    public boolean validate(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                log.warn("文件不是有效的图片: {}", file.getAbsolutePath());
                return false;
            }
            log.info("图片验证成功: {}x{}", image.getWidth(), image.getHeight());
            return true;
        } catch (Exception e) {
            log.error("图片验证失败", e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> extractMetadata(File file) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                metadata.put("width", image.getWidth());
                metadata.put("height", image.getHeight());
                metadata.put("type", image.getType());
                metadata.put("colorModel", image.getColorModel().toString());
            }
        } catch (Exception e) {
            log.error("提取图片元数据失败", e);
        }
        return metadata;
    }
    
    @Override
    protected String getFilePrefix() {
        return "img_";
    }
    
    @Override
    protected String getDefaultExtension() {
        return "jpg";
    }
    
    private MediaTypeEnum getMediaTypeFromUrl(String url) {
        // 先通过 URL 扩展名判断
        String lowerUrl = url.toLowerCase();
        for (String ext : MediaTypeEnum.IMAGE.getExtensions()) {
            if (lowerUrl.contains("." + ext)) {
                return MediaTypeEnum.IMAGE;
            }
        }
        
        // 再通过 HEAD 请求判断
        MediaTypeEnum type = null;
        try {
            MediaInfoDTO info = getMediaInfo(url);
            if (info != null) {
                type = info.getMediaType();
            }
        } catch (Exception e) {
            log.warn("通过 HEAD 请求判断媒体类型失败: {}", url);
        }
        
        return type;
    }
}