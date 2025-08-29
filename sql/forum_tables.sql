-- ----------------------------
-- 论坛功能数据库表
-- ----------------------------

-- 论坛分类表
DROP TABLE IF EXISTS `forum_category`;
CREATE TABLE `forum_category` (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) NOT NULL COMMENT '分类名称',
  `category_desc` varchar(200) DEFAULT NULL COMMENT '分类描述',
  `category_icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='论坛分类表';

-- 论坛帖子表
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `user_id` bigint(20) NOT NULL COMMENT '发帖用户ID',
  `title` varchar(200) NOT NULL COMMENT '帖子标题',
  `content` longtext NOT NULL COMMENT '帖子内容',
  `content_type` char(1) DEFAULT '1' COMMENT '内容类型（1文本 2富文本 3Markdown）',
  `view_count` bigint(20) DEFAULT 0 COMMENT '浏览次数',
  `reply_count` bigint(20) DEFAULT 0 COMMENT '回复次数',
  `like_count` bigint(20) DEFAULT 0 COMMENT '点赞次数',
  `is_top` char(1) DEFAULT '0' COMMENT '是否置顶（0否 1是）',
  `is_essence` char(1) DEFAULT '0' COMMENT '是否精华（0否 1是）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1审核中 2已删除）',
  `last_reply_time` datetime DEFAULT NULL COMMENT '最后回复时间',
  `last_reply_user_id` bigint(20) DEFAULT NULL COMMENT '最后回复用户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_last_reply_time` (`last_reply_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- 论坛回复表
DROP TABLE IF EXISTS `forum_reply`;
CREATE TABLE `forum_reply` (
  `reply_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) NOT NULL COMMENT '回复用户ID',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父回复ID（0表示直接回复帖子）',
  `content` text NOT NULL COMMENT '回复内容',
  `like_count` bigint(20) DEFAULT 0 COMMENT '点赞次数',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1审核中 2已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`reply_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='论坛回复表';

-- 论坛点赞表
DROP TABLE IF EXISTS `forum_like`;
CREATE TABLE `forum_like` (
  `like_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_type` char(1) NOT NULL COMMENT '目标类型（1帖子 2回复）',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='论坛点赞表';

-- 论坛收藏表
DROP TABLE IF EXISTS `forum_favorite`;
CREATE TABLE `forum_favorite` (
  `favorite_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='论坛收藏表';

-- 论坛标签表
DROP TABLE IF EXISTS `forum_tag`;
CREATE TABLE `forum_tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) DEFAULT '#409EFF' COMMENT '标签颜色',
  `use_count` bigint(20) DEFAULT 0 COMMENT '使用次数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='论坛标签表';

-- 帖子标签关联表
DROP TABLE IF EXISTS `forum_post_tag`;
CREATE TABLE `forum_post_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_tag` (`post_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='帖子标签关联表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 插入论坛分类数据
INSERT INTO `forum_category` VALUES 
(1, '技术交流', '技术问题讨论、经验分享', 'el-icon-cpu', 1, '0', 'admin', NOW(), 'admin', NOW(), '技术相关讨论'),
(2, '产品讨论', '产品功能建议、使用反馈', 'el-icon-shopping-bag-1', 2, '0', 'admin', NOW(), 'admin', NOW(), '产品相关讨论'),
(3, '闲聊灌水', '轻松话题、日常交流', 'el-icon-chat-dot-round', 3, '0', 'admin', NOW(), 'admin', NOW(), '休闲娱乐'),
(4, '公告通知', '官方公告、重要通知', 'el-icon-bell', 0, '0', 'admin', NOW(), 'admin', NOW(), '官方公告');

-- 插入标签数据
INSERT INTO `forum_tag` VALUES 
(1, 'Java', '#FF6B6B', 0, NOW()),
(2, 'Spring Boot', '#4ECDC4', 0, NOW()),
(3, 'Vue', '#45B7D1', 0, NOW()),
(4, 'MySQL', '#96CEB4', 0, NOW()),
(5, 'Redis', '#FFEAA7', 0, NOW()),
(6, 'Docker', '#DDA0DD', 0, NOW()),
(7, '问题', '#FF9999', 0, NOW()),
(8, '分享', '#99CCFF', 0, NOW()),
(9, '教程', '#99FF99', 0, NOW()),
(10, '求助', '#FFCC99', 0, NOW());

