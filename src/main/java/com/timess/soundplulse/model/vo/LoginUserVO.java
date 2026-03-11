package com.timess.soundplulse.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 当前登录用户信息视图
 */
@Schema(description = "当前登录用户信息")
@Data
public class LoginUserVO implements Serializable {
    private static final long serialVersionUID = -6878355451312782724L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户头像URL")
    private String userAvatar;

    @Schema(description = "用户简介")
    private String userProfile;

    @Schema(description = "用户角色：user/vip/admin")
    private String userRole;

    @Schema(description = "会员过期时间")
    private Date vipExpireTime;

    @Schema(description = "会员兑换码")
    private String vipCode;

    @Schema(description = "会员编号")
    private Long vipNumber;

    @Schema(description = "分享码")
    private String shareCode;

    @Schema(description = "邀请用户ID")
    private Long inviteUser;

    @Schema(description = "用户状态：0-正常，1-封禁")
    private Integer status;

    @Schema(description = "注册时间")
    private Date createTime;
}
