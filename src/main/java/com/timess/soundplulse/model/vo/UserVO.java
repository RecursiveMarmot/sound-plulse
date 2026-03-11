package com.timess.soundplulse.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息视图
 */
@Schema(description = "用户信息视图")
@Data
public class UserVO implements Serializable {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "用户邮箱")
    private String mail;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户头像URL")
    private String userAvatar;

    @Schema(description = "用户简介")
    private String userProfile;

    @Schema(description = "用户角色")
    private String userRole;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "用户状态：0-正常，1-封禁")
    private int status;

    private static final long serialVersionUID = 1L;
}
