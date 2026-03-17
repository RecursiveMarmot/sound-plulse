package com.timess.soundplulse.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户-歌曲关系表
 * @TableName user_song
 */
@TableName(value ="user_song")
@Data
public class UserSong implements Serializable {
    /**
     * 自增id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 歌曲id
     */
    private Long songId;

    /**
     * 关系类型（1-喜欢，2-最近播放，3-收藏）
     */
    private Integer relationType;

    /**
     * 播放次数
     */
    private Integer playCount;

    /**
     * 最后播放时间
     */
    private Date lastPlayTime;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
