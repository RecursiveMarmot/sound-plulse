package com.timess.soundpulse.strategy;

import com.timess.soundpulse.constant.MediaTypeEnum;
import com.timess.soundpulse.model.dto.common.MediaInfoDTO;

import java.io.File;
import java.util.Map;

/**
 * 媒体策略模式
 */
public interface MediaStrategy {
    
    /**
     * 获取支持的媒体类型
     * 该方法用于返回当前支持的媒体类型枚举值
     * @return 返回支持的媒体类型枚举值
     */
    MediaTypeEnum getSupportedType();


    /**
     * 判定url所指向的文件类型是否支持
     * @param url
     * @return
     */
    boolean isSupported(String url);

    /**
     * 获取媒体信息
     * @param url
     * @return
     */
    MediaInfoDTO getMediaInfo(String url);

    /**
     * 根据url下载媒体文件
     * @param url
     * @return
     * @throws Exception
     */
    File download(String url) throws Exception;

    /**
     * 验证url是否有效
     * @param file
     * @return
     */
    boolean validate(File file);

    /**
     * 获取媒体元数据
     * @param file
     * @return
     */
    Map<String, Object> extractMetadata(File file);
}