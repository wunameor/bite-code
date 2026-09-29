SET NAMES utf8mb4;
USE live_blog;

-- 定时重置：清空并恢复初始测试数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE blog_info;
TRUNCATE TABLE user_info;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `user_info` (`id`, `user_name`, `password`, `github_url`, `delete_flag`, `create_time`, `update_time`) VALUES (1, 'zhangsan', 'ce12fdcc46910b9721b08c836ad050555309b4df2d2a4fc3941d6b01c3f8c8ff', 'https://gitee.com/bubble-fish666/class-java45', 0, '2026-09-25 20:28:56', '2026-09-28 17:40:21');
INSERT INTO `user_info` (`id`, `user_name`, `password`, `github_url`, `delete_flag`, `create_time`, `update_time`) VALUES (2, 'lisi', 'bf761f7d0ca3a35f0bd1386f7b49ad37f662bb9fdaf04799a1498355bdf81f51', 'https://gitee.com/bubble-fish666/class-java45', 0, '2026-09-25 20:28:56', '2026-09-28 17:38:17');

INSERT INTO `blog_info` (`id`, `title`, `content`, `user_id`, `delete_flag`, `create_time`, `update_time`) VALUES (1, '第一篇博客', '111我是博客正文我是博客正文我是博客正文', 1, 0, '2026-09-25 20:28:56', '2026-09-25 20:28:56');
INSERT INTO `blog_info` (`id`, `title`, `content`, `user_id`, `delete_flag`, `create_time`, `update_time`) VALUES (2, '第二篇博客', '222我是博客正文我是博客正文我是博客正文', 2, 0, '2026-09-25 20:28:56', '2026-09-25 20:28:56');
INSERT INTO `blog_info` (`id`, `title`, `content`, `user_id`, `delete_flag`, `create_time`, `update_time`) VALUES (3, '博客更新测试-标题2221', '# 博客更新测试-正文2221\n\n1. 写前端\n2. 写后端', 1, 0, '2026-09-27 15:23:47', '2026-09-27 16:34:10');
INSERT INTO `blog_info` (`id`, `title`, `content`, `user_id`, `delete_flag`, `create_time`, `update_time`) VALUES (4, '博客添加测试', '##博客添加测试\n1. 写后端，用 ApiFox 测试\n2. 写前端，测试', 1, 1, '2026-09-27 15:30:15', '2026-09-27 16:13:41');
INSERT INTO `blog_info` (`id`, `title`, `content`, `user_id`, `delete_flag`, `create_time`, `update_time`) VALUES (6, '测试添加标题1111', '测试添加正文11111', 1, 0, '2026-09-27 16:22:59', '2026-09-27 16:22:59');
INSERT INTO `blog_info` (`id`, `title`, `content`, `user_id`, `delete_flag`, `create_time`, `update_time`) VALUES (7, 'add test', '## add test \ntttt', 1, 1, '2026-09-27 16:42:37', '2026-09-27 16:42:52');
