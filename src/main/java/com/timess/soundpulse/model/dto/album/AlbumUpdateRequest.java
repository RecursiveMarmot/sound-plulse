package com.timess.soundpulse.model.dto.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新专辑请求
 */
@Schema(description = "更新专辑请求")
@Data
public class AlbumUpdateRequest implements Serializable {

    @Schema(description = "专辑ID")
    private Long id;

    @Schema(description = "专辑名")
    private String albumName;

    @Schema(description = "歌手ID")
    private Long artistId;

    @Schema(description = "歌手名")
    private String artistName;

    @Schema(description = "专辑封面URL")
    private String coverUrl;

    @Schema(description = "专辑描述")
    private String description;

    @Schema(description = "发行日期")
    private Date releaseDate;

    private static final long serialVersionUID = 1L;
}
