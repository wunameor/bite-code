-- 抽奖异步任务表：既记录请求状态，也作为 RabbitMQ 投递失败后的补偿依据
CREATE TABLE IF NOT EXISTS draw_request (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，同时作为 requestId',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    activity_id BIGINT NOT NULL COMMENT '活动 id',
    prize_id BIGINT NOT NULL COMMENT '奖品 id',
    winner_ids TEXT NOT NULL COMMENT '请求中的中奖人 id 列表，JSON 数组',
    status VARCHAR(32) NOT NULL COMMENT 'PENDING/PROCESSING/SUCCESS/FAILED',
    error_code INT NULL COMMENT '失败错误码',
    error_message VARCHAR(255) NULL COMMENT '失败原因',
    result_json TEXT NULL COMMENT '成功后的中奖人员快照，JSON 数组',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '补偿重试次数',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_status_modified (status, gmt_modified) USING BTREE,
    KEY idx_activity_prize (activity_id, prize_id) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb3 ROW_FORMAT = DYNAMIC COMMENT = '抽奖异步任务';

-- 一个用户在一个活动内最多中一个奖品；执行前需确认历史数据无冲突
SET @uk_exists := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'winning_record'
      AND index_name = 'uk_w_a_id'
);
SET @uk_sql := IF(
    @uk_exists = 0,
    'ALTER TABLE winning_record ADD UNIQUE KEY uk_w_a_id (winner_id, activity_id) USING BTREE',
    'SELECT 1'
);
PREPARE uk_stmt FROM @uk_sql;
EXECUTE uk_stmt;
DEALLOCATE PREPARE uk_stmt;

-- 历史数据修正：已结束活动中仍为待中奖的人员，统一标记为未中奖
UPDATE activity_user au
JOIN activity a ON a.id = au.activity_id
SET au.status = 'NOT_WON'
WHERE au.status = 'PENDING'
  AND a.status = 'FINISHED';
