-- 初始化默认管理员（可重复执行，唯一键冲突时自动跳过）
-- 账号：admin
-- 密码：12345678
-- 邮箱：admin@lottery.com
-- 手机：13800000000
--
-- phone_number 是 13800000000 用项目 AES key 1234567890abcdef 加密后的 hex，
-- password 是 12345678 的 SHA-256，与 UserServiceImpl 的写入方式一致。
INSERT IGNORE INTO `user`
    (`user_name`, `email`, `phone_number`, `password`, `identity`)
VALUES (
    'admin',
    'admin@lottery.com',
    'aecfab0ed4c227c4de11135116c9eb32',
    'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',
    0
);
