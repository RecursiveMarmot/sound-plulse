package com.timess.soundplulse.model.dto.artist;

import com.timess.soundplulse.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * 查询歌手请求
 */
@Schema(description = "查询歌手请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class ArtistQueryRequest extends PageRequest implements Serializable {

    @Schema(description = "歌手ID")
    private Long id;

    @Schema(description = "歌手名")
    private String artistName;

    @Schema(description = "所属地区")
    private String region;

    private static final long serialVersionUID = 1L;
}
