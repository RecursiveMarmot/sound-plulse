package com.timess.soundpulse.model.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 歌词行对象（用于前端逐字滚动）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "歌词行")
public class LyricLine implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "时间戳（毫秒）", example = "12500")
    private Integer time;
    
    @Schema(description = "歌词内容", example = "We're no strangers to love")
    private String content;
    
    @Schema(description = "格式化的时间（mm:ss）", example = "00:12")
    private String formattedTime;
}