package com.timess.soundplulse.manager;

import org.springframework.web.multipart.MultipartFile;

public interface UploadStrategy {

    /**
     * 上传文件到cos存储桶
     * @param multipartFile 文件
     * @return 文件存储地址
     */
    String upload(MultipartFile multipartFile);
}
