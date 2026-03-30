package com.timess.soundpluse.manager;

import java.io.File;

public interface UploadStrategy {

    /**
     * 上传文件到cos存储桶
     * @param file 文件
     * @return 文件存储地址
     */
    String upload(File file);
}
