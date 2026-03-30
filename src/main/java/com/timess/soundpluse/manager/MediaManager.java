package com.timess.soundpluse.manager;

import com.timess.soundpluse.model.enums.FileTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
public class MediaManager {

    @Resource
    private UploadStrategyFactory factory;
    
    /**
     * 上传文件
     *
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
     *
     * @param file 文件
     * @param fileTypeEnum 文件类型枚举
     * @return
     */
    public String upload(File file, FileTypeEnum fileTypeEnum) {
        return upload(file, fileTypeEnum.getValue());
    }
}