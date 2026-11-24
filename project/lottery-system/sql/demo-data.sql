-- 演示数据：在 schema.sql 之后执行，假设业务表为空，且 user 表里的管理员已保留。
--
-- 约定：
--   - 普通用户 id 从 9001 开始，避免与管理员 id 冲突。
--   - 手机号密文与项目 EncryptTypeHandler 一致（AES/ECB/PKCS5，key = 1234567890abcdef）。
--   - 奖品图片使用项目内静态资源，路径为相对 URL，删除项目目录即可完全清理。
--   - 普通用户不提供前台登录入口，password 置空即可。
SET NAMES utf8mb4;

-- ----------------------------
-- 普通用户（20 人）
-- ----------------------------
INSERT INTO `user` (`id`, `user_name`, `email`, `phone_number`, `password`, `identity`) VALUES
(9001, '张伟', 'demo01@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000001', '1234567890abcdef'))), NULL, 1),
(9002, '王芳', 'demo02@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000002', '1234567890abcdef'))), NULL, 1),
(9003, '李娜', 'demo03@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000003', '1234567890abcdef'))), NULL, 1),
(9004, '刘洋', 'demo04@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000004', '1234567890abcdef'))), NULL, 1),
(9005, '陈晨', 'demo05@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000005', '1234567890abcdef'))), NULL, 1),
(9006, '杨帆', 'demo06@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000006', '1234567890abcdef'))), NULL, 1),
(9007, '赵磊', 'demo07@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000007', '1234567890abcdef'))), NULL, 1),
(9008, '黄敏', 'demo08@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000008', '1234567890abcdef'))), NULL, 1),
(9009, '周杰', 'demo09@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000009', '1234567890abcdef'))), NULL, 1),
(9010, '吴静', 'demo10@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000010', '1234567890abcdef'))), NULL, 1),
(9011, '徐涛', 'demo11@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000011', '1234567890abcdef'))), NULL, 1),
(9012, '孙丽', 'demo12@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000012', '1234567890abcdef'))), NULL, 1),
(9013, '马超', 'demo13@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000013', '1234567890abcdef'))), NULL, 1),
(9014, '朱琳', 'demo14@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000014', '1234567890abcdef'))), NULL, 1),
(9015, '胡军', 'demo15@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000015', '1234567890abcdef'))), NULL, 1),
(9016, '郭强', 'demo16@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000016', '1234567890abcdef'))), NULL, 1),
(9017, '何雪', 'demo17@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000017', '1234567890abcdef'))), NULL, 1),
(9018, '高鹏', 'demo18@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000018', '1234567890abcdef'))), NULL, 1),
(9019, '林悦', 'demo19@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000019', '1234567890abcdef'))), NULL, 1),
(9020, '罗浩', 'demo20@lottery.com', LOWER(HEX(AES_ENCRYPT('13900000020', '1234567890abcdef'))), NULL, 1);

-- ----------------------------
-- 奖品（6 个，图片均位于 src/main/resources/static/pic/demo）
-- ----------------------------
INSERT INTO `prize` (`id`, `name`, `description`, `price`, `image_url`) VALUES
(101, '旗舰智能手机', '年度旗舰机型，12GB + 256GB', 5999.00, '/pic/demo/phone.svg'),
(102, '无线降噪耳机', '头戴式主动降噪，续航 40 小时', 1299.00, '/pic/demo/headphones.svg'),
(103, '智能手表', '全天候健康监测，支持运动模式', 899.00, '/pic/demo/watch.svg'),
(104, '不锈钢保温杯', '316 不锈钢内胆，500ml', 199.00, '/pic/demo/thermos.svg'),
(105, '定制帆布袋', '加厚帆布，日常通勤容量', 59.00, '/pic/demo/tote-bag.svg'),
(106, '机械键盘', '87 键紧凑布局，热插拔轴体', 499.00, '/pic/demo/keyboard.svg');

-- ----------------------------
-- 活动（2 个进行中 + 1 个已结束）
-- ----------------------------
INSERT INTO `activity` (`id`, `activity_name`, `description`, `status`) VALUES
(201, '2026 年会抽奖（现场演示）', '全部奖项未抽取，可直接现场演示完整流程', 'RUNNING'),
(202, '2025 年终答谢抽奖（已结束）', '已抽取全部奖项，用于演示中奖名单和分享链接', 'FINISHED'),
(203, '2026 部门抽奖（进行中）', '二等奖已抽完，三等奖待抽取，用于演示中途状态', 'RUNNING');

-- ----------------------------
-- 活动奖品关联
-- 奖品总数量不能超过参与人数，顺序按一等奖、二等奖、三等奖抽取
-- ----------------------------
INSERT INTO `activity_prize`
(`id`, `activity_id`, `prize_id`, `prize_amount`, `prize_tiers`, `status`) VALUES
-- 201：全部待抽取
(301, 201, 101, 1, 'FIRST_PRIZE', 'PENDING'),
(302, 201, 102, 2, 'SECOND_PRIZE', 'PENDING'),
(303, 201, 103, 1, 'SECOND_PRIZE', 'PENDING'),
(304, 201, 104, 3, 'THIRD_PRIZE', 'PENDING'),
(305, 201, 105, 4, 'THIRD_PRIZE', 'PENDING'),
(306, 201, 106, 2, 'THIRD_PRIZE', 'PENDING'),
-- 202：全部已抽满
(307, 202, 101, 1, 'FIRST_PRIZE', 'DRAWN'),
(308, 202, 102, 2, 'SECOND_PRIZE', 'DRAWN'),
(309, 202, 103, 1, 'SECOND_PRIZE', 'DRAWN'),
(310, 202, 104, 3, 'THIRD_PRIZE', 'DRAWN'),
(311, 202, 105, 4, 'THIRD_PRIZE', 'DRAWN'),
(312, 202, 106, 2, 'THIRD_PRIZE', 'DRAWN'),
-- 203：二等奖已抽满，三等奖待抽取
(313, 203, 102, 2, 'SECOND_PRIZE', 'DRAWN'),
(314, 203, 106, 2, 'THIRD_PRIZE', 'PENDING');

-- ----------------------------
-- 活动人员关联
-- ----------------------------
INSERT INTO `activity_user` (`id`, `activity_id`, `user_id`, `user_name`, `status`) VALUES
-- 201：20 人全部待中奖
(4001, 201, 9001, '张伟', 'PENDING'),
(4002, 201, 9002, '王芳', 'PENDING'),
(4003, 201, 9003, '李娜', 'PENDING'),
(4004, 201, 9004, '刘洋', 'PENDING'),
(4005, 201, 9005, '陈晨', 'PENDING'),
(4006, 201, 9006, '杨帆', 'PENDING'),
(4007, 201, 9007, '赵磊', 'PENDING'),
(4008, 201, 9008, '黄敏', 'PENDING'),
(4009, 201, 9009, '周杰', 'PENDING'),
(4010, 201, 9010, '吴静', 'PENDING'),
(4011, 201, 9011, '徐涛', 'PENDING'),
(4012, 201, 9012, '孙丽', 'PENDING'),
(4013, 201, 9013, '马超', 'PENDING'),
(4014, 201, 9014, '朱琳', 'PENDING'),
(4015, 201, 9015, '胡军', 'PENDING'),
(4016, 201, 9016, '郭强', 'PENDING'),
(4017, 201, 9017, '何雪', 'PENDING'),
(4018, 201, 9018, '高鹏', 'PENDING'),
(4019, 201, 9019, '林悦', 'PENDING'),
(4020, 201, 9020, '罗浩', 'PENDING'),
-- 202：13 人中奖，7 人未中奖
(4021, 202, 9001, '张伟', 'WINNER'),
(4022, 202, 9002, '王芳', 'WINNER'),
(4023, 202, 9003, '李娜', 'WINNER'),
(4024, 202, 9004, '刘洋', 'WINNER'),
(4025, 202, 9005, '陈晨', 'WINNER'),
(4026, 202, 9006, '杨帆', 'WINNER'),
(4027, 202, 9007, '赵磊', 'WINNER'),
(4028, 202, 9008, '黄敏', 'WINNER'),
(4029, 202, 9009, '周杰', 'WINNER'),
(4030, 202, 9010, '吴静', 'WINNER'),
(4031, 202, 9011, '徐涛', 'WINNER'),
(4032, 202, 9012, '孙丽', 'WINNER'),
(4033, 202, 9013, '马超', 'WINNER'),
(4034, 202, 9014, '朱琳', 'NOT_WON'),
(4035, 202, 9015, '胡军', 'NOT_WON'),
(4036, 202, 9016, '郭强', 'NOT_WON'),
(4037, 202, 9017, '何雪', 'NOT_WON'),
(4038, 202, 9018, '高鹏', 'NOT_WON'),
(4039, 202, 9019, '林悦', 'NOT_WON'),
(4040, 202, 9020, '罗浩', 'NOT_WON'),
-- 203：2 人中奖，8 人继续参与三等奖
(4041, 203, 9001, '张伟', 'WINNER'),
(4042, 203, 9002, '王芳', 'WINNER'),
(4043, 203, 9003, '李娜', 'PENDING'),
(4044, 203, 9004, '刘洋', 'PENDING'),
(4045, 203, 9005, '陈晨', 'PENDING'),
(4046, 203, 9006, '杨帆', 'PENDING'),
(4047, 203, 9007, '赵磊', 'PENDING'),
(4048, 203, 9008, '黄敏', 'PENDING'),
(4049, 203, 9009, '周杰', 'PENDING'),
(4050, 203, 9010, '吴静', 'PENDING');

-- ----------------------------
-- 中奖记录
-- 同一活动内 winner_id 不能重复；每个奖品的记录数必须等于 prize_amount
-- ----------------------------
INSERT INTO `winning_record`
(`id`, `activity_id`, `activity_name`, `prize_id`, `prize_name`, `prize_tier`,
 `winner_id`, `winner_name`, `winner_email`, `winner_phone_number`, `winning_time`) VALUES
-- 202：一等奖 1 人
(501, 202, '2025 年终答谢抽奖（已结束）', 101, '旗舰智能手机', 'FIRST_PRIZE',
 9001, '张伟', 'demo01@lottery.com', '13900000001', DATE_SUB(NOW(), INTERVAL 30 DAY)),
-- 202：二等奖 2 + 1 人
(502, 202, '2025 年终答谢抽奖（已结束）', 102, '无线降噪耳机', 'SECOND_PRIZE',
 9002, '王芳', 'demo02@lottery.com', '13900000002', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 2 MINUTE),
(503, 202, '2025 年终答谢抽奖（已结束）', 102, '无线降噪耳机', 'SECOND_PRIZE',
 9003, '李娜', 'demo03@lottery.com', '13900000003', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 3 MINUTE),
(504, 202, '2025 年终答谢抽奖（已结束）', 103, '智能手表', 'SECOND_PRIZE',
 9004, '刘洋', 'demo04@lottery.com', '13900000004', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 4 MINUTE),
-- 202：三等奖 3 + 4 + 2 人
(505, 202, '2025 年终答谢抽奖（已结束）', 104, '不锈钢保温杯', 'THIRD_PRIZE',
 9005, '陈晨', 'demo05@lottery.com', '13900000005', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 5 MINUTE),
(506, 202, '2025 年终答谢抽奖（已结束）', 104, '不锈钢保温杯', 'THIRD_PRIZE',
 9006, '杨帆', 'demo06@lottery.com', '13900000006', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 6 MINUTE),
(507, 202, '2025 年终答谢抽奖（已结束）', 104, '不锈钢保温杯', 'THIRD_PRIZE',
 9007, '赵磊', 'demo07@lottery.com', '13900000007', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 7 MINUTE),
(508, 202, '2025 年终答谢抽奖（已结束）', 105, '定制帆布袋', 'THIRD_PRIZE',
 9008, '黄敏', 'demo08@lottery.com', '13900000008', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 8 MINUTE),
(509, 202, '2025 年终答谢抽奖（已结束）', 105, '定制帆布袋', 'THIRD_PRIZE',
 9009, '周杰', 'demo09@lottery.com', '13900000009', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 9 MINUTE),
(510, 202, '2025 年终答谢抽奖（已结束）', 105, '定制帆布袋', 'THIRD_PRIZE',
 9010, '吴静', 'demo10@lottery.com', '13900000010', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 10 MINUTE),
(511, 202, '2025 年终答谢抽奖（已结束）', 105, '定制帆布袋', 'THIRD_PRIZE',
 9011, '徐涛', 'demo11@lottery.com', '13900000011', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 11 MINUTE),
(512, 202, '2025 年终答谢抽奖（已结束）', 106, '机械键盘', 'THIRD_PRIZE',
 9012, '孙丽', 'demo12@lottery.com', '13900000012', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 12 MINUTE),
(513, 202, '2025 年终答谢抽奖（已结束）', 106, '机械键盘', 'THIRD_PRIZE',
 9013, '马超', 'demo13@lottery.com', '13900000013', DATE_SUB(NOW(), INTERVAL 30 DAY) + INTERVAL 13 MINUTE),
-- 203：二等奖已抽 2 人
(514, 203, '2026 部门抽奖（进行中）', 102, '无线降噪耳机', 'SECOND_PRIZE',
 9001, '张伟', 'demo01@lottery.com', '13900000001', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(515, 203, '2026 部门抽奖（进行中）', 102, '无线降噪耳机', 'SECOND_PRIZE',
 9002, '王芳', 'demo02@lottery.com', '13900000002', DATE_SUB(NOW(), INTERVAL 2 HOUR) + INTERVAL 1 MINUTE);
