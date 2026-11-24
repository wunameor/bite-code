-- 重置演示数据：只清理业务数据和普通用户，管理员（identity = '0'）原样保留。
--
-- 使用方式：
--   deploy/reset-demo-data.sh --yes
--
-- 执行顺序由脚本保证：
--   1. 先检查管理员是否存在；
--   2. 执行本文件（删除普通用户 + 业务表）；
--   3. 执行 schema.sql 重建表结构；
--   4. 执行 demo-data.sql 写入演示数据。
SET NAMES utf8mb4;

-- 只删除普通用户；管理员 id、密码哈希、邮箱、手机号、更新时间都不动。
DELETE FROM `user` WHERE `identity` <> '0' OR `identity` IS NULL;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `draw_request`;
DROP TABLE IF EXISTS `winning_record`;
DROP TABLE IF EXISTS `activity_user`;
DROP TABLE IF EXISTS `activity_prize`;
DROP TABLE IF EXISTS `activity`;
DROP TABLE IF EXISTS `prize`;
SET FOREIGN_KEY_CHECKS = 1;
