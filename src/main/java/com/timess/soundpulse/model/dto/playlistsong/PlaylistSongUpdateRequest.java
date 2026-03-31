package com.timess.soundpulse.model.dto.playlistsong;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新歌单-歌曲关联请求
 */
@Schema(description = "更新歌单-歌曲关联请求")
@Data
public class PlaylistSongUpdateRequest implements Serializable {

    @Schema(description = "关联ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    private static final long serialVersionUID = 1L;
}
