package com.timess.soundplulse.model.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

/**
 * 歌手视图
 */
@Schema(description = "歌手视图")
@Data
public class ArtistVO implements Serializable {

    @Schema(description = "歌手ID")
    private Long id;

    @Schema(description = "歌手名")
    private String artistName;

    @Schema(description = "歌手头像URL")
    private String artistAvatar;

    @Schema(description = "歌手简介")
    private String artistProfile;

    @Schema(description = "所属地区")
    private String region;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
