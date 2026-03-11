package com.timess.soundplulse.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 */
@Schema(description = "用户更新请求")
@Data
public class UserUpdateRequest implements Serializable {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户头像URL")
    private String userAvatar;

    @Schema(description = "用户简介")
    private String userProfile;

    @Schema(description = "用户状态：0-正常，1-封禁")
    private int status;

    @Schema(description = "用户角色：user/admin")
    private String userRole;

    private static final long serialVersionUID = 1L;
}
