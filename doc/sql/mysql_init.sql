CREATE DATABASE IF NOT EXISTS `afinit_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `afinit_blog`;

CREATE TABLE `tb_user` (
                           `id` bigint(20) NOT NULL COMMENT '主键ID（雪花ID）',
                           `username` varchar(50) NOT NULL COMMENT '用户名/账号',
                           `password` varchar(100) NOT NULL COMMENT '加密后的密码',
                           `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
                           `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                           `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
                           `role` tinyint(4) NOT NULL DEFAULT '0' COMMENT '角色：0-普通用户，1-管理员',
                           `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '帐号状态：0-停用，1-正常',
                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：1-删除，0-未删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_username` (`username`) COMMENT '唯一索引：防止账号重复',
                           KEY `idx_status_create_time` (`status`,`create_time`) COMMENT '复合索引：方便后台按状态和时间筛选用户'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户用户信息表';

CREATE TABLE `tb_blog` (
                           `id` bigint(20) NOT NULL COMMENT '主键ID（雪花ID）',
                           `user_id` bigint(20) NOT NULL COMMENT '归属用户ID/作者ID',
                           `title` varchar(128) NOT NULL COMMENT '文章标题',
                           `summary` varchar(256) DEFAULT NULL COMMENT '文章摘要（用于列表展示）',
                           `content` longtext NOT NULL COMMENT 'Markdown纯文本正文（含图片公网链接）',
                           `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '发布状态：0-草稿，1-已发布',
                           `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '阅读量（实际由Redis承载高频写，定时刷入此字段）',
                           `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数（实际由Redis承载高频写，定时刷入此字段）',
                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除,1-删除,0-未删除',
                           PRIMARY KEY (`id`),
                           KEY `idx_user_id` (`user_id`) COMMENT '普通索引：优化查询个人博客列表的性能',
                           KEY `idx_status_create_time` (`status`,`create_time`) COMMENT '复合索引：优化首页列表的倒序查询性能'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';



CREATE TABLE `tb_blog_barrage` (
                                   `id` bigint(20) NOT NULL COMMENT '主键ID（雪花ID）',
                                   `blog_id` bigint(20) NOT NULL COMMENT '所属文章ID',
                                   `user_id` bigint(20) NOT NULL COMMENT '发送用户ID',
                                   `content` varchar(255) NOT NULL COMMENT '弹幕内容',
                                   `scroll_percent` decimal(5,2) NOT NULL COMMENT '触发位置百分比(例如 50.25)',
                                   `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-隐藏，1-正常',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：1-删除，0-未删除',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_blog_id` (`blog_id`) COMMENT '普通索引：用于拉取指定文章的全部弹幕'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章弹幕表';