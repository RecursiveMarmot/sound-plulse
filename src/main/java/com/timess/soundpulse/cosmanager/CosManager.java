package com.timess.soundpulse.cosmanager;

import com.timess.soundpulse.model.enums.FileTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CosManager {

    @Resource
    private UploadStrategyFactory factory;
    
    /**
     * 上传文件
     * @param file 文件
     * @param type 类型 (image, audio, video)
     * @return
     */
    public String upload(File file, String type) {
        // 1. 获取对应的策略
        UploadStrategy strategy = factory.getStrategy(type);
        // 2. 执行上传
        return strategy.upload(file);
    }

    /**
     * 上传文件
     * @param file 文件
     * @param fileTypeEnum 文件类型枚举
     * @return
     */
    public String upload(File file, FileTypeEnum fileTypeEnum) {
        return upload(file, fileTypeEnum.getValue());
    }

    /**
     * 删除文件
     * @param url 文件
     * @param fileTypeEnum 文件类型枚举
     * @return
     */
    public void delete(String url, FileTypeEnum fileTypeEnum) {
        // 1. 获取对应的策略
        UploadStrategy strategy = factory.getStrategy(fileTypeEnum.getValue());
        // 2. 执行删除操作
        strategy.deleteByUrl(url);
    }
}