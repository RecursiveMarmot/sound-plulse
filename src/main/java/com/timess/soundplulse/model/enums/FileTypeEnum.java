package com.timess.soundplulse.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件类型枚举
 */
@Getter
public enum FileTypeEnum {

    IMAGE("图片", "image", Arrays.asList("jpg", "jpeg", "png", "gif", "webp"), 5 * 1024 * 1024L),
    AUDIO("音频", "audio", Arrays.asList("mp3", "wav", "flac", "ogg"), 20 * 1024 * 1024L),
    VIDEO("视频", "video", Arrays.asList("mp4", "avi", "mkv", "mov"), 100 * 1024 * 1024L);

    private final String text;

    private final String value;

    private final List<String> suffixList;

    private final Long maxSize;

    FileTypeEnum(String text, String value, List<String> suffixList, Long maxSize) {
        this.text = text;
        this.value = value;
        this.suffixList = suffixList;
        this.maxSize = maxSize;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (FileTypeEnum anEnum : FileTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
