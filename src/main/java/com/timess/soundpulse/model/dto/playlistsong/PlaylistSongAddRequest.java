package com.timess.soundpulse.model.dto.playlistsong;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加歌曲到歌单请求
 */
@Schema(description = "添加歌曲到歌单请求")
@Data
public class PlaylistSongAddRequest implements Serializable {

    @Schema(description = "歌单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long playlistId;

    @Schema(description = "歌曲ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long songId;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    private static final long serialVersionUID = 1L;
}
