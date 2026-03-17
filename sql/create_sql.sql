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



-- 创建歌手表
CREATE TABLE IF NOT EXISTS `artist` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `artist_name` VARCHAR(255) NOT NULL COMMENT '歌手名',
    `artist_avatar` VARCHAR(1024) DEFAULT NULL COMMENT '歌手头像',
    `artist_profile` TEXT DEFAULT NULL COMMENT '歌手简介',
    `region` VARCHAR(50) DEFAULT NULL COMMENT '所属地区',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` INT DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌手表';
CREATE INDEX idx_artist_name ON `artist`(`artist_name`);
CREATE INDEX idx_artist_id ON `song`(`artist_id`);
-- 创建歌曲表
CREATE TABLE IF NOT EXISTS `song` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `song_name` VARCHAR(255) NOT NULL COMMENT '歌曲名',
    `artist_id` BIGINT NOT NULL COMMENT '歌手id',
    `artistName` VARCHAR(255) NOT NULL COMMENT '歌手名字',
    `album_name` VARCHAR(255) DEFAULT NULL COMMENT '专辑名',
    `song_url` VARCHAR(1024) NOT NULL COMMENT '歌曲链接',
    `cover_url` VARCHAR(1024) DEFAULT NULL COMMENT '封面链接',
    `lyrics` TEXT DEFAULT NULL COMMENT '歌词',
    `duration` INT DEFAULT NULL COMMENT '时长（秒）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` INT DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌曲表';

CREATE INDEX idx_song_name ON `song`(`song_name`);


-- 1. 专辑表（建议添加）
CREATE TABLE IF NOT EXISTS `album` (
                                       `id` BIGINT NOT NULL COMMENT 'id',
                                       `album_name` VARCHAR(255) NOT NULL COMMENT '专辑名',
                                       `artist_id` BIGINT NOT NULL COMMENT '歌手id',
                                       `artist_name` VARCHAR(255) NOT NULL COMMENT '歌手名',
                                       `cover_url` VARCHAR(1024) DEFAULT NULL COMMENT '专辑封面',
                                       `description` TEXT DEFAULT NULL COMMENT '专辑描述',
                                       `release_date` DATE DEFAULT NULL COMMENT '发行日期',
                                       `song_count` INT DEFAULT 0 COMMENT '歌曲数量',
                                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `is_delete` INT DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专辑表';

CREATE INDEX idx_album_name ON `album`(`album_name`);
CREATE INDEX idx_album_artist ON `album`(`artist_id`);
CREATE INDEX idx_album_date ON `album`(`release_date`);

-- 2. 歌单表
CREATE TABLE IF NOT EXISTS `playlist` (
                                          `id` BIGINT NOT NULL COMMENT 'id',
                                          `user_id` BIGINT NOT NULL COMMENT '创建者用户id',
                                          `playlist_name` VARCHAR(255) NOT NULL COMMENT '歌单名称',
                                          `cover_url` VARCHAR(1024) DEFAULT NULL COMMENT '歌单封面',
                                          `description` VARCHAR(512) DEFAULT NULL COMMENT '歌单描述',
                                          `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签（用逗号分隔）',
                                          `is_public` INT DEFAULT 1 COMMENT '是否公开（0-私有，1-公开）',
                                          `song_count` INT DEFAULT 0 COMMENT '歌曲数量',
                                          `play_count` INT DEFAULT 0 COMMENT '播放次数',
                                          `like_count` INT DEFAULT 0 COMMENT '收藏次数',
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `is_delete` INT DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌单表';

CREATE INDEX idx_playlist_user ON `playlist`(`user_id`);
CREATE INDEX idx_playlist_public ON `playlist`(`is_public`, `create_time`);

-- 3. 歌单-歌曲关联表
CREATE TABLE IF NOT EXISTS `playlist_song` (
                                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                               `playlist_id` BIGINT NOT NULL COMMENT '歌单id',
                                               `song_id` BIGINT NOT NULL COMMENT '歌曲id',
                                               `sort_order` INT DEFAULT 0 COMMENT '排序序号',
                                               `add_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                                               PRIMARY KEY (`id`),
                                               UNIQUE KEY uk_playlist_song (`playlist_id`, `song_id`),
                                               INDEX idx_song_id (`song_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌单-歌曲关联表';

-- 4. 用户-歌曲关系表（喜欢/收藏）
CREATE TABLE IF NOT EXISTS `user_song` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                           `user_id` BIGINT NOT NULL COMMENT '用户id',
                                           `song_id` BIGINT NOT NULL COMMENT '歌曲id',
                                           `relation_type` INT DEFAULT 1 COMMENT '关系类型（1-喜欢，2-最近播放，3-收藏）',
                                           `play_count` INT DEFAULT 0 COMMENT '播放次数',
                                           `last_play_time` DATETIME DEFAULT NULL COMMENT '最后播放时间',
                                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY uk_user_song (`user_id`, `song_id`, `relation_type`),
                                           INDEX idx_song_user (`song_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-歌曲关系表';

-- 5. 用户-歌单关系表（收藏歌单）
CREATE TABLE IF NOT EXISTS `user_playlist` (
                                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                               `user_id` BIGINT NOT NULL COMMENT '用户id',
                                               `playlist_id` BIGINT NOT NULL COMMENT '歌单id',
                                               `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                                               PRIMARY KEY (`id`),
                                               UNIQUE KEY uk_user_playlist (`user_id`, `playlist_id`),
                                               INDEX idx_playlist_user (`playlist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-歌单关系表';