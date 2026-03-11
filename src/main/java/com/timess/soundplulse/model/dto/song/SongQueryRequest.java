package com.timess.soundplulse.model.dto.song;

import com.timess.soundplulse.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * 查询歌曲请求
 */
@Schema(description = "查询歌曲请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class SongQueryRequest extends PageRequest implements Serializable {

    @Schema(description = "歌曲ID")
    private Long id;

    @Schema(description = "歌曲名称")
    private String songName;

    @Schema(description = "歌手ID")
    private Long artistId;

    @Schema(description = "专辑名")
    private String albumName;

    private static final long serialVersionUID = 1L;
}
