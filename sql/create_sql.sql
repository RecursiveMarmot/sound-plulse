create database if not exists `soundplulse` default character set utf8mb4 collate utf8mb4_unicode_ci;
-- 创建用户表
use soundplulse;
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` BIGINT NOT NULL COMMENT 'id',
                                      `user_account` VARCHAR(255) NOT NULL COMMENT '账号',
                                      `mail` VARCHAR(255) DEFAULT NULL COMMENT '用户邮箱',
                                      `user_password` VARCHAR(255) NOT NULL COMMENT '密码',
                                      `user_name` VARCHAR(255) DEFAULT NULL COMMENT '用户昵称',
                                      `user_avatar` VARCHAR(1024) DEFAULT NULL COMMENT '用户头像',
                                      `user_profile` VARCHAR(512) DEFAULT NULL COMMENT '用户简介',
                                      `user_role` VARCHAR(50) DEFAULT 'user' COMMENT '用户角色：user/vip/admin',
                                      `status` INT DEFAULT 0 COMMENT '0--正常，1--封禁',
                                      `edit_time` DATETIME DEFAULT NULL COMMENT '编辑时间',
                                      `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `is_delete` INT DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建索引（提高查询效率）
CREATE INDEX idx_user_account ON `user`(`user_account`);
CREATE INDEX idx_mail ON `user`(`mail`);
-- 添加唯一约束
-- 如果要求账号唯一
ALTER TABLE `user` ADD UNIQUE INDEX uk_user_account (`user_account`);
-- 如果要求邮箱唯一
ALTER TABLE `user` ADD UNIQUE INDEX uk_mail (`mail`);