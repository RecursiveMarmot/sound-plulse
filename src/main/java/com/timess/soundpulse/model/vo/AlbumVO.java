package com.timess.soundpulse.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 专辑响应vo对象
 */
@Data
public class AlbumVO implements Serializable {
    /**
     * id
     */
    @Schema(description = "专辑ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 专辑名
     */
    @Schema(description = "专辑名")
    private String albumName;

    /**
     * 歌手id
     */
    @Schema(description = "歌手ID")
    private Long artistId;

    /**
     * 歌手名
     */
    @Schema(description = "歌手名称")
    private String artistName;

    /**
     * 专辑封面
     */
    @Schema(description = "专辑封面URL")
    private String coverUrl;

    /**
     * 专辑描述
     */
    @Schema(description = "专辑描述")
    private String description;

    /**
     * 发行日期
     */
    @Schema(description = "发行日期")
    private Date releaseDate;

    /**
     * 歌曲数量
     */
    @Schema(description = "歌曲数量")
    private Integer songCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
