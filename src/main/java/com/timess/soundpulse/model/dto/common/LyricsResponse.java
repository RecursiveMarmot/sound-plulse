package com.timess.soundpulse.model.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 歌词响应
 */
@Data
@Schema(description = "歌词响应")
public class LyricsResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "是否成功")
    private Boolean success;
    
    @Schema(description = "消息")
    private String message;
    
    @Schema(description = "歌词数据")
    private LyricsData data;
    
    @Data
    @Schema(description = "歌词数据")
    public static class LyricsData implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        @Schema(description = "歌曲名")
        private String trackName;
        
        @Schema(description = "歌手名")
        private String artistName;
        
        @Schema(description = "专辑名")
        private String albumName;
        
        @Schema(description = "纯文本歌词")
        private String plainLyrics;
        
        @Schema(description = "同步歌词（LRC格式）")
        private String syncedLyrics;
        
        @Schema(description = "歌曲时长（秒）")
        private Integer duration;
        
        @Schema(description = "歌词语言")
        private String language;
        
        @Schema(description = "歌词ID")
        private Long lyricsId;
        
        @Schema(description = "解析后的歌词时间轴")
        private List<LyricLine> timeline;
    }
}