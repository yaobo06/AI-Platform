-- 数据库连接测试脚本
-- 请在MySQL客户端中执行此脚本

-- 1. 检查数据库连接
SELECT 'Database connection test' as test_name, NOW() as current_time;

-- 2. 检查现有表
SHOW TABLES;

-- 3. 检查论坛相关表是否存在
SHOW TABLES LIKE 'forum%';

-- 4. 如果论坛表不存在，执行创建脚本
-- 请先执行 sql/forum_tables.sql 文件

-- 5. 检查表结构
DESCRIBE forum_category;
DESCRIBE forum_post;
DESCRIBE forum_reply;
DESCRIBE forum_like;
DESCRIBE forum_favorite;
DESCRIBE forum_tag;
DESCRIBE forum_post_tag;

-- 6. 检查初始数据
SELECT * FROM forum_category;
SELECT * FROM forum_tag;

-- 7. 检查用户表结构（用于关联查询）
DESCRIBE sys_user;
