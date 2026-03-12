package com.timess.soundplulse.model.dto.song;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * 创建歌曲请求
 */
@Schema(description = "创建歌曲请求")
@Data
public class SongAddRequest implements Serializable {

    @Schema(description = "歌曲名称")
    private String songName;

    @Schema(description = "歌手ID")
    private Long artistId;

    @Schema(description = "专辑名")
    private String albumName;

    @Schema(description = "歌词")
    private String lyrics;

    @Schema(description = "时长（秒）")
    private Integer duration;

    private static final long serialVersionUID = 1L;
}
