package com.timess.soundplulse.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送验证码请求
 */
@Schema(description = "发送注册验证码请求")
@Data
public class UserSendRegisterMailRequest implements Serializable {

    @Schema(description = "用户邮箱地址")
    private String mail;

    private static final long serialVersionUID = 1L;
}
