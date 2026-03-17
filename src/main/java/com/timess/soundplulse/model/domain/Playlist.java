package com.timess.soundplulse.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 歌单表
 * @TableName playlist
 */
@TableName(value ="playlist")
@Data
public class Playlist implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建者用户id
     */
    private Long userId;

    /**
     * 歌单名称
     */
    private String playlistName;

    /**
     * 歌单封面
     */
    private String coverUrl;

    /**
     * 歌单描述
     */
    private String description;

    /**
     * 标签（用逗号分隔）
     */
    private String tags;

    /**
     * 是否公开（0-私有，1-公开）
     */
    private Integer isPublic;

    /**
     * 歌曲数量
     */
    private Integer songCount;

    /**
     * 播放次数
     */
    private Integer playCount;

    /**
     * 收藏次数
     */
    private Integer likeCount;

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
