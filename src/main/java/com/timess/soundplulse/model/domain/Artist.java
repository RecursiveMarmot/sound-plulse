package com.timess.soundplulse.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 歌手
 * @TableName artist
 */
@TableName(value ="artist")
@Data
public class Artist implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 歌手名
     */
    private String artistName;

    /**
     * 歌手头像
     */
    private String artistAvatar;

    /**
     * 歌手简介
     */
    private String artistProfile;

    /**
     * 所属地区
     */
    private String region;

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
