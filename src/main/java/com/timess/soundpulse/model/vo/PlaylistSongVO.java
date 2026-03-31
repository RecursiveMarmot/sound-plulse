package com.timess.soundpulse.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 歌单-歌曲关联视图
 */
@Schema(description = "歌单-歌曲关联视图")
@Data
public class PlaylistSongVO implements Serializable {

    @Schema(description = "关联ID")
    private Long id;

    @Schema(description = "歌单ID")
    private Long playlistId;

    @Schema(description = "歌曲ID")
    private Long songId;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "添加时间")
    private Date addTime;

    @Schema(description = "歌曲详细信息")
    private SongVO song;

    private static final long serialVersionUID = 1L;
}
