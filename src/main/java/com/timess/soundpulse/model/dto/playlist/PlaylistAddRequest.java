package com.timess.soundpulse.model.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建歌单请求
 */
@Schema(description = "创建歌单请求")
@Data
public class PlaylistAddRequest implements Serializable {

    @Schema(description = "创建者用户ID")
    private Long userId;

    @Schema(description = "歌单名称")
    private String playlistName;

    @Schema(description = "歌单封面")
    private String coverUrl;

    @Schema(description = "歌单描述")
    private String description;

    @Schema(description = "标签（用逗号分隔）")
    private String tags;

    @Schema(description = "是否公开（0-私有，1-公开）")
    private Integer isPublic;

    private static final long serialVersionUID = 1L;
}
