package com.timess.soundpulse.model.dto.playlistsong;

import com.timess.soundpulse.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询歌单-歌曲关联请求
 */
@Schema(description = "查询歌单-歌曲关联请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class PlaylistSongQueryRequest extends PageRequest implements Serializable {

    @Schema(description = "关联ID")
    private Long id;

    @Schema(description = "歌单ID")
    private Long playlistId;

    @Schema(description = "歌曲ID")
    private Long songId;

    private static final long serialVersionUID = 1L;
}
