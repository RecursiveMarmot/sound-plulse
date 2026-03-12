package com.timess.soundplulse.manager;

import com.timess.soundplulse.model.enums.FileTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class UploadStrategyFactory {

    @Resource
    private Map<String, UploadStrategy> strategyMap;  // Spring会自动注入所有策略
    
    public UploadStrategy getStrategy(String type) {
        FileTypeEnum fileTypeEnum = FileTypeEnum.getEnumByValue(type);
        if (fileTypeEnum == null) {
            throw new IllegalArgumentException("不支持的文件类型: " + type);
        }
        String strategyName = type + "UploadStrategy";
        UploadStrategy strategy = strategyMap.get(strategyName);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到对应的上传策略: " + strategyName);
        }
        return strategy;
    }
}