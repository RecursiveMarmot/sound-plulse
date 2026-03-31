package com.timess.soundpulse.model.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 歌词搜索请求
 */
@Data
@Schema(description = "歌词搜索请求")
public class LyricsSearchRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "歌曲名不能为空")
    @Schema(description = "歌曲名", example = "Never Gonna Give You Up")
    private String trackName;
    
    @NotBlank(message = "歌手名不能为空")
    @Schema(description = "歌手名", example = "Rick Astley")
    private String artistName;
    
    @Schema(description = "专辑名", example = "Whenever You Need Somebody")
    private String albumName;
    
    @Schema(description = "歌曲时长（秒）", example = "213")
    private Integer duration;
}