package com.timess.soundplulse.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */
@Schema(description = "用户登录请求")
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -4839454500897686724L;

    @Schema(description = "用户账号")
    String userAccount;

    @Schema(description = "用户密码")
    String userPassword;

}
