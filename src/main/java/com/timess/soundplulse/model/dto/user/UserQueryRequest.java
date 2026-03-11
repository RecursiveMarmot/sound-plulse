package com.timess.soundplulse.model.dto.user;

import com.timess.soundplulse.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 */
@Schema(description = "用户查询请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "用户简介")
    private String userProfile;

    @Schema(description = "用户角色：user/admin/ban")
    private String userRole;

    private static final long serialVersionUID = 1L;
}
