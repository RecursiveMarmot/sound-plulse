package com.timess.soundpulse.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 歌单响应VO对象
 */
@Data
public class PlaylistVO implements Serializable {

    @Schema(description = "歌单ID")
    private Long id;

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

    @Schema(description = "歌曲数量")
    private Integer songCount;

    @Schema(description = "播放次数")
    private Integer playCount;

    @Schema(description = "收藏次数")
    private Integer likeCount;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
