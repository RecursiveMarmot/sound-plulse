package com.timess.soundpulse.model.dto.common;

import com.timess.soundpulse.constant.MediaTypeEnum;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;


/**
 * 媒体信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaInfoDTO {
    /**
     * url地址
     */
    private String url;

    /**
     * 文件类型
     */
    private MediaTypeEnum mediaType;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件大小
     */
    private long contentLength;

    /**
     * 最后修改时间
     */
    private long lastModified;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 扩展名
     */
    private String extension;

    /**
     * 是否支持
     */
    private boolean supported;
    private Map<String, Object> metadata;
}