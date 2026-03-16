package com.timess.soundplulse.manager;

import com.timess.soundplulse.model.enums.FileTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Component
public class MediaManager {

    @Resource
    private UploadStrategyFactory factory;
    
    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param type 类型 (image, audio, video)
     * @return
     */
    public String upload(MultipartFile multipartFile, String type) {
        // 1. 获取对应的策略
        UploadStrategy strategy = factory.getStrategy(type);
        // 2. 执行上传
        return strategy.upload(multipartFile);
    }

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param fileTypeEnum 文件类型枚举
     * @return
     */
    public String upload(MultipartFile multipartFile, FileTypeEnum fileTypeEnum) {
        return upload(multipartFile, fileTypeEnum.getValue());
    }
}