package com.timess.soundplulse.model.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

/**
 * 歌曲视图
 */
@Schema(description = "歌曲视图")
@Data
public class SongVO implements Serializable {

    @Schema(description = "歌曲ID")
    private Long id;

    @Schema(description = "歌曲名称")
    private String songName;

    @Schema(description = "歌手ID")
    private Long artistId;

    @Schema(description = "专辑名")
    private String albumName;

    @Schema(description = "歌曲链接")
    private String songUrl;

    @Schema(description = "封面链接")
    private String coverUrl;

    @Schema(description = "歌词")
    private String lyrics;

    @Schema(description = "时长（秒）")
    private Integer duration;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
