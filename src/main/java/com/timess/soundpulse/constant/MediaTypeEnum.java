package com.timess.soundpulse.constant;

import lombok.Getter;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum MediaTypeEnum {
    
    IMAGE("image", "图片", 
          new HashSet<>(Arrays.asList(
              MediaType.IMAGE_JPEG_VALUE,
              MediaType.IMAGE_PNG_VALUE,
              MediaType.IMAGE_GIF_VALUE,
              "image/bmp",
              "image/webp",
              "image/svg+xml"
          )),
          new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg")),
          10 * 1024 * 1024), // 10MB
    
    AUDIO("audio", "音频",
          new HashSet<>(Arrays.asList(
              "audio/mpeg",
              "audio/mp3",
              "audio/wav",
              "audio/ogg",
              "audio/flac",
              "audio/aac",
              "audio/x-m4a",
              "audio/mp4"
          )),
          new HashSet<>(Arrays.asList("mp3", "wav", "ogg", "flac", "aac", "m4a", "mp4")),
          50 * 1024 * 1024), // 50MB
    
    VIDEO("video", "视频",
          new HashSet<>(Arrays.asList(
              "video/mp4",
              "video/mpeg",
              "video/quicktime",
              "video/x-msvideo",
              "video/webm",
              "video/x-matroska"
          )),
          new HashSet<>(Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "webm", "mkv")),
          200 * 1024 * 1024); // 200MB
    
    private final String code;
    private final String name;
    private final Set<String> contentTypes;
    private final Set<String> extensions;
    private final long maxSize;
    
    MediaTypeEnum(String code, String name, Set<String> contentTypes, 
                  Set<String> extensions, long maxSize) {
        this.code = code;
        this.name = name;
        this.contentTypes = contentTypes;
        this.extensions = extensions;
        this.maxSize = maxSize;
    }
    
    public static MediaTypeEnum fromContentType(String contentType) {
        if (contentType == null) return null;
        String lowerType = contentType.toLowerCase();
        for (MediaTypeEnum type : values()) {
            if (type.contentTypes.contains(lowerType)) {
                return type;
            }
        }
        return null;
    }
    
    public static MediaTypeEnum fromExtension(String extension) {
        if (extension == null) return null;
        String lowerExt = extension.toLowerCase().replace(".", "");
        for (MediaTypeEnum type : values()) {
            if (type.extensions.contains(lowerExt)) {
                return type;
            }
        }
        return null;
    }
}