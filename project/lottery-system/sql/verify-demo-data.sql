-- 演示数据校验：任何一条违规都会出现在结果集里，空结果表示数据有效。
-- 使用方式：
--   mysql -u root -p -D lottery_system < sql/verify-demo-data.sql
SET NAMES utf8mb4;

SELECT '普通用户数量不是 20' AS violation
FROM DUAL
WHERE (SELECT COUNT(*) FROM `user` WHERE `identity` = '1') <> 20

UNION ALL
SELECT '管理员数量小于 1'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `user` WHERE `identity` = '0') < 1

UNION ALL
SELECT '奖品数量不是 6'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `prize`) <> 6

UNION ALL
SELECT '活动数量不是 3'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `activity`) <> 3

UNION ALL
SELECT '活动奖品关联数量不是 14'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `activity_prize`) <> 14

UNION ALL
SELECT '活动人员关联数量不是 50'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `activity_user`) <> 50

UNION ALL
SELECT '中奖记录数量不是 15'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `winning_record`) <> 15

UNION ALL
SELECT '重置后仍存在抽奖任务'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `draw_request`) <> 0

UNION ALL
SELECT '活动 201 存在非待抽取的奖品'
FROM `activity_prize`
WHERE `activity_id` = 201 AND `status` <> 'PENDING'
GROUP BY `activity_id`
HAVING COUNT(*) > 0

UNION ALL
SELECT '活动 201 参与人数少于奖品总数量'
FROM `activity_prize` ap
WHERE ap.`activity_id` = 201
GROUP BY ap.`activity_id`
HAVING SUM(ap.`prize_amount`) > (SELECT COUNT(*) FROM `activity_user` WHERE `activity_id` = 201)

UNION ALL
SELECT '活动 201 存在中奖记录'
FROM `winning_record`
WHERE `activity_id` = 201
GROUP BY `activity_id`
HAVING COUNT(*) > 0

UNION ALL
SELECT CONCAT('活动 ', ap.`activity_id`, ' 奖品 ', ap.`prize_id`, ' 中奖人数与名额不一致')
FROM `activity_prize` ap
LEFT JOIN (
    SELECT `activity_id`, `prize_id`, COUNT(*) AS winner_count
    FROM `winning_record`
    GROUP BY `activity_id`, `prize_id`
) w ON w.`activity_id` = ap.`activity_id` AND w.`prize_id` = ap.`prize_id`
WHERE ap.`status` = 'DRAWN' AND COALESCE(w.winner_count, 0) <> ap.`prize_amount`

UNION ALL
SELECT CONCAT('活动 ', ap.`activity_id`, ' 待抽取奖品已存在中奖记录')
FROM `activity_prize` ap
JOIN (
    SELECT `activity_id`, `prize_id`, COUNT(*) AS winner_count
    FROM `winning_record`
    GROUP BY `activity_id`, `prize_id`
) w ON w.`activity_id` = ap.`activity_id` AND w.`prize_id` = ap.`prize_id`
WHERE ap.`status` = 'PENDING' AND w.winner_count > 0

UNION ALL
SELECT '活动 202 不是已完成状态'
FROM DUAL
WHERE (SELECT `status` FROM `activity` WHERE `id` = 202) <> 'FINISHED'

UNION ALL
SELECT '活动 202 存在未抽满的奖品'
FROM `activity_prize`
WHERE `activity_id` = 202 AND `status` <> 'DRAWN'
GROUP BY `activity_id`
HAVING COUNT(*) > 0

UNION ALL
SELECT '活动 202 人员状态数量不一致'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `activity_user` WHERE `activity_id` = 202 AND `status` = 'WINNER') <> 13
   OR (SELECT COUNT(*) FROM `activity_user` WHERE `activity_id` = 202 AND `status` = 'NOT_WON') <> 7

UNION ALL
SELECT '活动 203 奖品状态不正确'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `activity_prize` WHERE `activity_id` = 203 AND `status` = 'DRAWN') <> 1
   OR (SELECT COUNT(*) FROM `activity_prize` WHERE `activity_id` = 203 AND `status` = 'PENDING') <> 1

UNION ALL
SELECT '活动 203 中奖人员数量不一致'
FROM DUAL
WHERE (SELECT COUNT(*) FROM `activity_user` WHERE `activity_id` = 203 AND `status` = 'WINNER') <> 2

UNION ALL
SELECT CONCAT('中奖记录 ', wr.`id`, ' 的 winner 不是该活动的中奖人员')
FROM `winning_record` wr
LEFT JOIN `activity_user` au
    ON au.`activity_id` = wr.`activity_id` AND au.`user_id` = wr.`winner_id`
WHERE au.`id` IS NULL OR au.`status` <> 'WINNER'

UNION ALL
SELECT CONCAT('活动奖品 ', ap.`id`, ' 引用了不存在的奖品')
FROM `activity_prize` ap
LEFT JOIN `prize` p ON p.`id` = ap.`prize_id`
WHERE p.`id` IS NULL

UNION ALL
SELECT CONCAT('活动人员 ', au.`id`, ' 引用了不存在的用户')
FROM `activity_user` au
LEFT JOIN `user` u ON u.`id` = au.`user_id`
WHERE u.`id` IS NULL

UNION ALL
SELECT '演示用户的手机号密文无法解密为合法手机号'
FROM `user`
WHERE `identity` = '1'
  AND COALESCE(CONVERT(AES_DECRYPT(UNHEX(`phone_number`), '1234567890abcdef') USING utf8mb4), '') NOT REGEXP '^1[3-9][0-9]{9}$'
GROUP BY `identity`
HAVING COUNT(*) > 0

UNION ALL
SELECT CONCAT('已完成活动 ', a.`id`, ' 仍有待中奖人员')
FROM `activity` a
JOIN `activity_user` au ON au.`activity_id` = a.`id`
WHERE a.`status` = 'FINISHED' AND au.`status` = 'PENDING'
GROUP BY a.`id`;
