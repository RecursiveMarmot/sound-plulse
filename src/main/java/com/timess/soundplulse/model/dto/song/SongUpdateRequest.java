package com.timess.soundplulse.model.dto.song;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * 更新歌曲请求
 */
@Schema(description = "更新歌曲请求")
@Data
public class SongUpdateRequest implements Serializable {

    @Schema(description = "歌曲ID")
    private Long id;

    @Schema(description = "歌曲名称")
    private String songName;

    @Schema(description = "歌手ID")
    private Long artistId;

    @Schema(description = "专辑名")
    private String albumName;

    @Schema(description = "歌曲链接")
    private String songUrl;

    @Schema(description = "封面链接")
    private String coverUrl;

    @Schema(description = "歌词")
    private String lyrics;

    @Schema(description = "时长（秒）")
    private Integer duration;

    private static final long serialVersionUID = 1L;
}
