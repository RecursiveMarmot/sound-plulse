package com.timess.soundpulse.strategy;

import com.timess.soundpulse.constant.MediaTypeEnum;
import com.timess.soundpulse.model.dto.common.MediaInfoDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 媒体处理管理器 - 策略模式 + 工厂模式
 */
@Slf4j
@Component
public class MediaStrategyManager {
    
    @Autowired
    private List<MediaStrategy> strategies;
    
    private final Map<MediaTypeEnum, MediaStrategy> strategyMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        // 将策略注册到 map 中
        for (MediaStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedType(), strategy);
            log.info("注册媒体策略: {}", strategy.getSupportedType().getName());
        }
    }
    
    /**
     * 获取媒体类型
     */
    public MediaTypeEnum getMediaType(String url) {
        for (MediaStrategy strategy : strategies) {
            if (strategy.isSupported(url)) {
                return strategy.getSupportedType();
            }
        }
        return null;
    }
    
    /**
     * 获取媒体信息
     */
    public MediaInfoDTO getMediaInfo(String url) {
        MediaTypeEnum type = getMediaType(url);
        if (type == null) {
            log.warn("无法识别媒体类型: {}", url);
            return null;
        }
        
        MediaStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            log.warn("未找到对应的处理策略: {}", type);
            return null;
        }
        
        return strategy.getMediaInfo(url);
    }
    
    /**
     * 下载媒体文件
     */
    public File downloadMedia(String url) throws Exception {
        MediaTypeEnum type = getMediaType(url);
        if (type == null) {
            throw new IllegalArgumentException("无法识别的媒体类型: " + url);
        }
        
        MediaStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的媒体类型: " + type.getName());
        }
        
        log.info("开始下载媒体文件: {}, 类型: {}", url, type.getName());
        return strategy.download(url);
    }
    
    /**
     * 验证媒体文件
     */
    public boolean validateMedia(File file, MediaTypeEnum type) {
        MediaStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            return false;
        }
        return strategy.validate(file);
    }
    
    /**
     * 提取媒体元数据
     */
    public Map<String, Object> extractMetadata(File file, MediaTypeEnum type) {
        MediaStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            return new HashMap<>();
        }
        return strategy.extractMetadata(file);
    }
}