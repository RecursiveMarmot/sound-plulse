package com.timess.soundpulse.model.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 歌词搜索请求
 */
@Data
@Schema(description = "歌词ttml搜索请求")
public class TtmlLyricsRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "歌曲名", required = true)
    private String trackName;

//    @Schema(description = "歌手名", required = true)
//    private String artistName;
}
