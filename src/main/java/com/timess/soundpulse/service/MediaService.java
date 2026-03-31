package com.timess.soundpulse.service;

import com.timess.soundpulse.model.dto.common.MediaInfoDTO;

public interface MediaService {

    /**
     * 处理外部媒体文件
     * @param url
     * @return
     */
    String processExternalMedia(String url);

    /**
     * 处理图片
     * @param url
     * @param mediaInfo
     * @return
     * @throws Exception
     */
    String processImage(String url, MediaInfoDTO mediaInfo) throws Exception;

    /**
     * 处理音频
     * @param url
     * @param mediaInfo
     * @return
     * @throws Exception
     */
    String processAudio(String url, MediaInfoDTO mediaInfo) throws Exception;

    /**
     * 处理视频
     */
    String processVideo(String url, MediaInfoDTO mediaInfo) throws Exception;
}
