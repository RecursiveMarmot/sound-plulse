package com.timess.soundplulse.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 专辑表
 * @TableName album
 */
@TableName(value ="album")
@Data
public class Album implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 专辑名
     */
    private String albumName;

    /**
     * 歌手id
     */
    private Long artistId;

    /**
     * 歌手名
     */
    private String artistName;

    /**
     * 专辑封面
     */
    private String coverUrl;

    /**
     * 专辑描述
     */
    private String description;

    /**
     * 发行日期
     */
    private Date releaseDate;

    /**
     * 歌曲数量
     */
    private Integer songCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
