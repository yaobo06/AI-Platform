-- ----------------------------
-- AI模型表
-- ----------------------------
DROP TABLE IF EXISTS `ai_models`;
CREATE TABLE `ai_models` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '模型名称',
  `description` text COMMENT '模型描述',
  `image_url` varchar(500) DEFAULT NULL COMMENT '模型图片URL',
  `category_type` varchar(50) DEFAULT NULL COMMENT '分类类型：text/image/audio/video/code',
  `status_code` bigint(20) DEFAULT 1 COMMENT '状态码：1热门 2新品 3推荐',
  `version` varchar(50) DEFAULT NULL COMMENT '版本号',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签，逗号分隔',
  `experience_url` varchar(500) DEFAULT NULL COMMENT '体验链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_category_type` (`category_type`),
  KEY `idx_status_code` (`status_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='AI模型表';

-- ----------------------------
-- 初始化数据
-- ----------------------------
INSERT INTO `ai_models` VALUES 
(1, 'GPT-4', '最新一代大语言模型，具备强大的文本理解和生成能力', '/images/gpt4.jpg', 'text', 1, 'v4.0', '对话,GPT-4,自然语言处理', 'https://chat.openai.com', 'admin', NOW(), 'admin', NOW(), 'OpenAI开发的大语言模型'),
(2, 'Claude-3', '高性能AI助手，擅长复杂推理和长文本处理', '/images/claude3.jpg', 'text', 2, 'v3.0', 'Claude,对话,学术研究', 'https://claude.ai', 'admin', NOW(), 'admin', NOW(), 'Anthropic开发的AI助手'),
(3, 'DALL-E 3', '先进的AI图像生成模型，可根据文本描述创建图像', '/images/dalle3.jpg', 'image', 1, 'v3.0', '图像生成,艺术创作,AI绘画', 'https://openai.com/dall-e-3', 'admin', NOW(), 'admin', NOW(), 'OpenAI的图像生成模型'),
(4, 'Midjourney', '专业的AI艺术创作工具，生成高质量图像', '/images/midjourney.jpg', 'image', 3, 'v6.0', '艺术创作,精确控制,AI绘画', 'https://midjourney.com', 'admin', NOW(), 'admin', NOW(), '专业AI绘画工具'),
(5, 'Whisper', 'OpenAI的语音识别模型，支持多种语言', '/images/whisper.jpg', 'audio', 1, 'v3.0', '语音识别,多语言,实时转录', 'https://openai.com/research/whisper', 'admin', NOW(), 'admin', NOW(), 'OpenAI的语音处理模型'),
(6, 'Sora', '文本到视频生成模型，可创建高质量视频内容', '/images/sora.jpg', 'video', 2, 'v1.0', '视频生成,动画制作,AI视频', 'https://openai.com/sora', 'admin', NOW(), 'admin', NOW(), 'OpenAI的视频生成模型'),
(7, 'GitHub Copilot', 'AI代码助手，帮助开发者提高编程效率', '/images/copilot.jpg', 'code', 1, 'v2.0', '代码生成,AI编程,智能补全', 'https://github.com/features/copilot', 'admin', NOW(), 'admin', NOW(), 'GitHub的AI编程助手'); 