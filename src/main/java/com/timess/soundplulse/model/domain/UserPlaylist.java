package com.timess.soundplulse.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户-歌单关系表
 * @TableName user_playlist
 */
@TableName(value ="user_playlist")
@Data
public class UserPlaylist implements Serializable {
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
     * 歌单id
     */
    private Long playlistId;

    /**
     * 收藏时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
