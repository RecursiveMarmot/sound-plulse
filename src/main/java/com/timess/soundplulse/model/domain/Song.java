package com.timess.soundplulse.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 歌曲
 * @TableName song
 */
@TableName(value ="song")
@Data
public class Song implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 歌曲名
     */
    private String songName;

    /**
     * 歌手id
     */
    private Long artistId;

    /**
     * 歌手名称
     */
    private String artistName;

    /**
     * 专辑名
     */
    private String albumName;

    /**
     * 歌曲链接
     */
    private String songUrl;

    /**
     * 封面链接
     */
    private String coverUrl;

    /**
     * 歌词
     */
    private String lyrics;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
