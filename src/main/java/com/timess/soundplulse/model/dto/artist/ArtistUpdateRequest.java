package com.timess.soundplulse.model.dto.artist;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * 更新歌手请求
 */
@Schema(description = "更新歌手请求")
@Data
public class ArtistUpdateRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}
