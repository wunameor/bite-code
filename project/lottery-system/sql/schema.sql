-- 抽奖系统完整表结构。
--
-- 使用方式：
--   1. Docker 首次启动时由 MySQL 容器自动执行（/docker-entrypoint-initdb.d/01-schema.sql）。
--   2. 手动初始化：mysql -u root -p -D lottery_system < sql/schema.sql
--
-- 说明：
--   - 只创建缺失的表，不删除数据库、不清空数据，可安全重复执行。
--   - 重置演示数据请使用 deploy/reset-demo-data.sh，不要改用 lottery_system.sql。
SET NAMES utf8mb4;

-- ----------------------------
-- 活动主表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `activity` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `activity_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '活动名称',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '活动描述',
  `status` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '活动状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id`(`id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '活动';

-- ----------------------------
-- 活动奖品关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `activity_prize` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `prize_id` bigint NOT NULL COMMENT '活动关联的奖品id',
  `prize_amount` bigint NOT NULL DEFAULT 1 COMMENT '关联奖品的数量',
  `prize_tiers` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '奖品等级',
  `status` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '活动奖品状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id`(`id` ASC) USING BTREE,
  UNIQUE INDEX `uk_a_p_id`(`activity_id` ASC, `prize_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '活动奖品关联';

-- ----------------------------
-- 活动人员关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `activity_user` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `user_id` bigint NOT NULL COMMENT '圈选的用户id',
  `user_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名',
  `status` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id`(`id` ASC) USING BTREE,
  UNIQUE INDEX `uk_a_u_id`(`activity_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '活动人员关联';

-- ----------------------------
-- 奖品主表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `prize` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '奖品名称',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '奖品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '奖品价值',
  `image_url` varchar(2048) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '奖品展示图',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id`(`id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '奖品';

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户姓名',
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '邮箱',
  `phone_number` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '手机号（AES 密文）',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '登录密码（SHA-256）',
  `identity` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户身份：0 管理员，1 普通用户',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id`(`id` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email`(30) ASC) USING BTREE,
  UNIQUE INDEX `uk_phone_number`(`phone_number`(11) ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '用户';

-- ----------------------------
-- 中奖记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `winning_record` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `activity_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '活动名称',
  `prize_id` bigint NOT NULL COMMENT '奖品id',
  `prize_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '奖品名称',
  `prize_tier` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '奖品等级',
  `winner_id` bigint NOT NULL COMMENT '中奖人id',
  `winner_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '中奖人姓名',
  `winner_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '中奖人邮箱',
  `winner_phone_number` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '中奖人电话',
  `winning_time` datetime NOT NULL COMMENT '中奖时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id`(`id` ASC) USING BTREE,
  -- 一个用户在一个活动内最多中一个奖品
  UNIQUE INDEX `uk_w_a_id`(`winner_id` ASC, `activity_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '中奖记录';

-- ----------------------------
-- 抽奖异步任务表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `draw_request` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，同时作为 requestId',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `activity_id` bigint NOT NULL COMMENT '活动 id',
  `prize_id` bigint NOT NULL COMMENT '奖品 id',
  `winner_ids` text NOT NULL COMMENT '请求中的中奖人 id 列表，JSON 数组',
  `status` varchar(32) NOT NULL COMMENT 'PENDING/PROCESSING/SUCCESS/FAILED',
  `error_code` int NULL COMMENT '失败错误码',
  `error_message` varchar(255) NULL COMMENT '失败原因',
  `result_json` text NULL COMMENT '成功后的中奖人员快照，JSON 数组',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '补偿重试次数',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_status_modified`(`status`, `gmt_modified`) USING BTREE,
  KEY `idx_activity_prize`(`activity_id`, `prize_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb3 ROW_FORMAT = DYNAMIC COMMENT = '抽奖异步任务';
