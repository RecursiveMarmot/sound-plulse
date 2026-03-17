package com.timess.soundplulse.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 歌单-歌曲关联表
 * @TableName playlist_song
 */
@TableName(value ="playlist_song")
@Data
public class PlaylistSong implements Serializable {
    /**
     * 自增id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 歌单id
     */
    private Long playlistId;

    /**
     * 歌曲id
     */
    private Long songId;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 添加时间
     */
    private Date addTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
