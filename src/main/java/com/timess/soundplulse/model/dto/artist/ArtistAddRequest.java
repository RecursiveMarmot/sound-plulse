package com.timess.soundplulse.model.dto.artist;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * 创建歌手请求
 */
@Schema(description = "创建歌手请求")
@Data
public class ArtistAddRequest implements Serializable {

    @Schema(description = "歌手名")
    private String artistName;

    @Schema(description = "歌手头像URL")
    private String artistAvatar;

    @Schema(description = "歌手简介")
    private String artistProfile;

    @Schema(description = "所属地区")
    private String region;

    private static final long serialVersionUID = 1L;
}
