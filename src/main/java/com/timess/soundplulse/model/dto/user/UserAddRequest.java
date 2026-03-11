package com.timess.soundplulse.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增用户接口
 */
@Schema(description = "新增用户/用户注册请求")
@Data
public class UserAddRequest implements Serializable {

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "账号")
    private String userAccount;

    @Schema(description = "用户邮箱")
    private String mail;

    @Schema(description = "用户密码")
    private String userPassword;

    @Schema(description = "邮箱验证码")
    private String verifyCode;

    @Schema(description = "用户头像URL")
    private String userAvatar;

    @Schema(description = "用户简介")
    private String userProfile;

    @Schema(description = "用户角色: user, admin")
    private String userRole;

    private static final long serialVersionUID = 1L;
}
