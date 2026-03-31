package com.timess.soundpulse.model.dto.album;

import com.timess.soundpulse.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询专辑请求
 */
@Schema(description = "查询专辑请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class AlbumQueryRequest extends PageRequest implements Serializable {

    @Schema(description = "专辑ID")
    private Long id;

    @Schema(description = "专辑名")
    private String albumName;

    @Schema(description = "歌手ID")
    private Long artistId;

    @Schema(description = "歌手名")
    private String artistName;

    private static final long serialVersionUID = 1L;
}
