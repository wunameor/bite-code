package com.wunamor.lotterysystem.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wunamor.lotterysystem.common.enums.ActivityStatusEnum;
import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.ActivityMapper;
import com.wunamor.lotterysystem.mapper.ActivityPrizeMapper;
import com.wunamor.lotterysystem.mapper.ActivityUserMapper;
import com.wunamor.lotterysystem.mapper.PrizeMapper;
import com.wunamor.lotterysystem.mapper.UserMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import com.wunamor.lotterysystem.mapper.dataobject.PrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import com.wunamor.lotterysystem.service.ActivityCacheService;
import com.wunamor.lotterysystem.service.DrawPrizeService;
import com.wunamor.lotterysystem.service.command.DrawPrizeCommand;
import com.wunamor.lotterysystem.service.draw.state.DrawContext;
import com.wunamor.lotterysystem.service.draw.state.DrawStateUpdaterChain;
import com.wunamor.lotterysystem.service.dto.DrawPrizeDTO;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.wunamor.lotterysystem.service.enums.ActivityUserStatusEnum;
import com.wunamor.lotterysystem.service.mq.message.DrawNotifyMessage;
import com.wunamor.lotterysystem.service.mq.producer.DrawMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽奖服务实现：数据库事务内完成校验和状态修改，事务提交后清缓存并投递通知。
 */
@Slf4j
@Service
public class DrawPrizeServiceImpl implements DrawPrizeService {

    private final ActivityMapper activityMapper;
    private final ActivityPrizeMapper activityPrizeMapper;
    private final ActivityUserMapper activityUserMapper;
    private final PrizeMapper prizeMapper;
    private final UserMapper userMapper;
    private final ActivityCacheService activityCacheService;
    private final DrawMessageProducer drawMessageProducer;
    private final DrawStateUpdaterChain drawStateUpdaterChain;

    public DrawPrizeServiceImpl(ActivityMapper activityMapper,
                                ActivityPrizeMapper activityPrizeMapper,
                                ActivityUserMapper activityUserMapper,
                                PrizeMapper prizeMapper,
                                UserMapper userMapper,
                                ActivityCacheService activityCacheService,
                                DrawMessageProducer drawMessageProducer,
                                DrawStateUpdaterChain drawStateUpdaterChain) {
        this.activityMapper = activityMapper;
        this.activityPrizeMapper = activityPrizeMapper;
        this.activityUserMapper = activityUserMapper;
        this.prizeMapper = prizeMapper;
        this.userMapper = userMapper;
        this.activityCacheService = activityCacheService;
        this.drawMessageProducer = drawMessageProducer;
        this.drawStateUpdaterChain = drawStateUpdaterChain;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DrawPrizeDTO drawPrize(DrawPrizeCommand command) {
        // 1. 校验基础参数，并保证中奖人员不重复
        checkDrawCommand(command);

        // 2. 锁定活动行，同一活动的抽奖请求串行执行
        ActivityDO activity = lockRunningActivity(command.getActivityId());

        // 3. 校验活动奖品仍待抽取，并且必须是当前顺序上应抽的奖品
        ActivityPrizeDO activityPrize = getPendingActivityPrize(
                command.getActivityId(), command.getPrizeId());
        PrizeDO prize = getPrize(activityPrize.getPrizeId());
        checkPrizeOrder(command.getActivityId(), command.getPrizeId());

        // 4. 校验中奖人数、人员待中奖状态和联系方式
        List<Long> winnerIds = command.getWinnerIds();
        checkWinnerCount(winnerIds, activityPrize);
        Map<Long, ActivityUserDO> activityUserById =
                queryPendingWinnerUsers(command.getActivityId(), winnerIds);
        Map<Long, UserDO> userById = queryWinnerUsers(winnerIds);

        // 5. 组装抽奖上下文，并由状态更新链完成同一事务内的状态翻转
        DrawContext context = buildContext(command, activity, activityPrize, prize,
                activityUserById, userById);
        drawStateUpdaterChain.update(context);

        // 6. 事务提交后清缓存并异步投递通知
        afterCommit(context.getActivityId(), buildNotifyMessages(context.getWinningRecords()));

        // 7. 组装抽奖结果
        return buildDrawPrizeDTO(context);
    }

    /**
     * 校验抽奖命令；参数为空或人员重复时直接返回业务错误。
     */
    private void checkDrawCommand(DrawPrizeCommand command) {
        // 1. 活动、奖品、中奖人员均不能为空
        if (command == null || command.getActivityId() == null
                || command.getPrizeId() == null
                || command.getWinnerIds() == null
                || command.getWinnerIds().isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_INFO_IS_EMPTY);
        }

        // 2. 人员 id 不能为空，且同一次抽奖不能重复
        Set<Long> uniqueIds = new HashSet<>();
        for (Long winnerId : command.getWinnerIds()) {
            if (winnerId == null) {
                throw new ServiceException(ServiceErrorCodeConstants.DRAW_WINNER_INVALID);
            }
            if (!uniqueIds.add(winnerId)) {
                throw new ServiceException(ServiceErrorCodeConstants.DRAW_WINNER_DUPLICATE);
            }
        }
    }

    /**
     * 锁定活动行并校验活动仍可抽奖。
     */
    private ActivityDO lockRunningActivity(Long activityId) {
        // 1. 行锁保证并发抽奖不会同时通过状态校验
        ActivityDO activity = activityMapper.selectByIdForUpdate(activityId);
        if (activity == null) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_NOT_FOUND);
        }

        // 2. 只有进行中的活动允许抽奖
        if (!ActivityStatusEnum.RUNNING.getCode().equals(activity.getStatus())) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_ACTIVITY_NOT_RUNNING);
        }
        return activity;
    }

    /**
     * 查询活动奖品并确认仍处于待抽取状态。
     */
    private ActivityPrizeDO getPendingActivityPrize(Long activityId, Long prizeId) {
        // 1. 按活动 + 奖品唯一键查询关联
        ActivityPrizeDO activityPrize = activityPrizeMapper.selectOne(
                new LambdaQueryWrapper<ActivityPrizeDO>()
                        .eq(ActivityPrizeDO::getActivityId, activityId)
                        .eq(ActivityPrizeDO::getPrizeId, prizeId));
        if (activityPrize == null) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_INVALID);
        }

        // 2. 已抽取的奖品不能重复抽
        if (!ActivityPrizeStatusEnum.PENDING.getCode().equals(activityPrize.getStatus())) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ALREADY_DRAWN);
        }
        return activityPrize;
    }

    /**
     * 校验请求的奖品是否是当前第一个待抽奖品，保证一等奖、二等奖、三等奖的顺序。
     */
    private void checkPrizeOrder(Long activityId, Long prizeId) {
        // 1. 按等级和创建顺序排列活动奖品
        List<ActivityPrizeDO> prizes = listOrderedPrizes(activityId);

        // 2. 找到第一个待抽奖品，请求奖品必须与它一致
        ActivityPrizeDO current = prizes.stream()
                .filter(prize -> ActivityPrizeStatusEnum.PENDING.getCode()
                        .equals(prize.getStatus()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ServiceErrorCodeConstants.DRAW_PRIZE_ALREADY_DRAWN));
        if (!current.getPrizeId().equals(prizeId)) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ORDER_INVALID);
        }
    }

    private List<ActivityPrizeDO> listOrderedPrizes(Long activityId) {
        List<ActivityPrizeDO> prizes = new ArrayList<>(activityPrizeMapper.selectList(
                new LambdaQueryWrapper<ActivityPrizeDO>()
                        .eq(ActivityPrizeDO::getActivityId, activityId)));
        prizes.sort(Comparator
                .comparingInt((ActivityPrizeDO prize) ->
                        PrizeTierEnum.orderOf(prize.getPrizeTiers()))
                .thenComparing(ActivityPrizeDO::getId));
        return prizes;
    }

    /**
     * 查询奖品主表，保证中奖记录能拿到奖品快照。
     */
    private PrizeDO getPrize(Long prizeId) {
        PrizeDO prize = prizeMapper.selectById(prizeId);
        if (prize == null) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_INVALID);
        }
        return prize;
    }

    /**
     * 校验中奖人数必须等于该奖品配置的数量。
     */
    private void checkWinnerCount(List<Long> winnerIds, ActivityPrizeDO activityPrize) {
        long expected = activityPrize.getPrizeAmount() == null
                ? 0L : activityPrize.getPrizeAmount();
        if (winnerIds.size() != expected) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_WINNER_COUNT_INVALID);
        }
    }

    /**
     * 查询本次活动内仍待中奖的中奖人员，返回 userId -> 活动人员映射。
     */
    private Map<Long, ActivityUserDO> queryPendingWinnerUsers(Long activityId,
                                                              List<Long> winnerIds) {
        List<ActivityUserDO> users = activityUserMapper.selectList(
                new LambdaQueryWrapper<ActivityUserDO>()
                        .eq(ActivityUserDO::getActivityId, activityId)
                        .in(ActivityUserDO::getUserId, winnerIds)
                        .eq(ActivityUserDO::getStatus, ActivityUserStatusEnum.PENDING.getCode()));
        if (users.size() != winnerIds.size()) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_WINNER_INVALID);
        }
        return users.stream().collect(Collectors.toMap(
                ActivityUserDO::getUserId, Function.identity()));
    }

    /**
     * 查询中奖人的邮箱和手机号，返回 userId -> 用户映射。
     */
    private Map<Long, UserDO> queryWinnerUsers(List<Long> winnerIds) {
        List<UserDO> users = userMapper.selectBatchIds(winnerIds);
        if (users.size() != winnerIds.size()) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_WINNER_CONTACT_INVALID);
        }
        return users.stream().collect(Collectors.toMap(
                UserDO::getId, Function.identity()));
    }

    /**
     * 组装状态更新链需要的上下文，并提前完成中奖记录快照构建。
     */
    private DrawContext buildContext(DrawPrizeCommand command,
                                     ActivityDO activity,
                                     ActivityPrizeDO activityPrize,
                                     PrizeDO prize,
                                     Map<Long, ActivityUserDO> activityUserById,
                                     Map<Long, UserDO> userById) {
        List<WinningRecordDO> records = buildWinningRecords(activity, activityPrize, prize,
                command.getWinnerIds(), activityUserById, userById, new Date());

        DrawContext context = new DrawContext();
        context.setRequestId(command.getRequestId());
        context.setActivityId(command.getActivityId());
        context.setPrizeId(command.getPrizeId());
        context.setWinnerIds(command.getWinnerIds());
        context.setActivity(activity);
        context.setActivityPrize(activityPrize);
        context.setPrize(prize);
        context.setActivityUserById(activityUserById);
        context.setUserById(userById);
        context.setWinningRecords(records);
        return context;
    }

    /**
     * 按请求中的人员顺序组装中奖记录快照。
     */
    private List<WinningRecordDO> buildWinningRecords(ActivityDO activity,
                                                      ActivityPrizeDO activityPrize,
                                                      PrizeDO prize,
                                                      List<Long> winnerIds,
                                                      Map<Long, ActivityUserDO> activityUserById,
                                                      Map<Long, UserDO> userById,
                                                      Date winningTime) {
        List<WinningRecordDO> records = new ArrayList<>(winnerIds.size());
        for (Long winnerId : winnerIds) {
            ActivityUserDO activityUser = activityUserById.get(winnerId);
            UserDO user = userById.get(winnerId);
            String phoneNumber = user == null ? null : getPhoneNumber(user);

            // 1. 中奖人必须有完整联系方式，否则不写半成品记录
            if (activityUser == null || user == null
                    || !StringUtils.hasText(user.getEmail())
                    || !StringUtils.hasText(phoneNumber)) {
                throw new ServiceException(
                        ServiceErrorCodeConstants.DRAW_WINNER_CONTACT_INVALID);
            }

            // 2. 中奖记录保存抽奖时的活动、奖品、人员快照
            records.add(buildWinningRecord(activity, activityPrize, prize,
                    activityUser, user, phoneNumber, winningTime));
        }
        return records;
    }

    /**
     * 构建单条中奖记录。
     */
    private WinningRecordDO buildWinningRecord(ActivityDO activity,
                                               ActivityPrizeDO activityPrize,
                                               PrizeDO prize,
                                               ActivityUserDO activityUser,
                                               UserDO user,
                                               String phoneNumber,
                                               Date winningTime) {
        WinningRecordDO record = new WinningRecordDO();
        record.setActivityId(activity.getId());
        record.setActivityName(activity.getActivityName());
        record.setPrizeId(prize.getId());
        record.setPrizeName(prize.getName());
        record.setPrizeTier(activityPrize.getPrizeTiers());
        record.setWinnerId(activityUser.getUserId());
        record.setWinnerName(activityUser.getUserName());
        record.setWinnerEmail(user.getEmail());
        record.setWinnerPhoneNumber(phoneNumber);
        record.setWinningTime(winningTime);
        return record;
    }

    /**
     * 读取用户解密后的手机号；用户或手机号为空时返回 null。
     */
    private String getPhoneNumber(UserDO user) {
        return user.getPhoneNumber() == null ? null : user.getPhoneNumber().getValue();
    }

    /**
     * 把中奖记录转换成异步通知消息。
     */
    private List<DrawNotifyMessage> buildNotifyMessages(List<WinningRecordDO> records) {
        List<DrawNotifyMessage> messages = new ArrayList<>(records.size());
        for (WinningRecordDO record : records) {
            DrawNotifyMessage message = new DrawNotifyMessage();
            message.setActivityId(record.getActivityId());
            message.setActivityName(record.getActivityName());
            message.setPrizeId(record.getPrizeId());
            message.setPrizeName(record.getPrizeName());
            message.setPrizeTier(record.getPrizeTier());
            PrizeTierEnum prizeTier = PrizeTierEnum.getByCode(record.getPrizeTier());
            message.setPrizeTierName(prizeTier == null
                    ? record.getPrizeTier() : prizeTier.getDesc());
            message.setWinnerId(record.getWinnerId());
            message.setWinnerName(record.getWinnerName());
            message.setWinnerEmail(record.getWinnerEmail());
            message.setWinningTime(DateUtil.formatDateTime(record.getWinningTime()));
            messages.add(message);
        }
        return messages;
    }

    /**
     * 注册事务提交后动作；无事务时直接执行，便于单元测试和直接调用。
     */
    private void afterCommit(Long activityId, List<DrawNotifyMessage> messages) {
        // 1. 无事务时直接执行
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            runAfterCommit(activityId, messages);
            return;
        }

        // 2. 有事务时等提交成功后再清缓存、投递通知，回滚时不产生副作用
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        runAfterCommit(activityId, messages);
                    }
                });
    }

    /**
     * 执行提交后动作：缓存删除和通知投递都按尽力而为处理，不影响抽奖结果。
     */
    private void runAfterCommit(Long activityId, List<DrawNotifyMessage> messages) {
        // 1. 删除活动详情快照，下次查询重新加载最新状态
        try {
            activityCacheService.evictActivityDetail(activityId);
        } catch (RuntimeException e) {
            log.error("抽奖后删除活动详情缓存失败，activityId: {}", activityId, e);
        }

        // 2. 每个中奖人独立投递，单条失败不影响其他通知
        for (DrawNotifyMessage message : messages) {
            try {
                drawMessageProducer.sendNotify(message);
            } catch (RuntimeException e) {
                log.error("抽奖通知投递失败，activityId: {}, winnerId: {}",
                        activityId, message.getWinnerId(), e);
            }
        }
    }

    /**
     * 组装抽奖结果。
     */
    private DrawPrizeDTO buildDrawPrizeDTO(DrawContext context) {
        DrawPrizeDTO dto = new DrawPrizeDTO();
        dto.setActivityId(context.getActivityId());
        dto.setPrizeId(context.getPrizeId());
        dto.setWinnerCount(context.getWinnerIds().size());
        dto.setActivityFinished(context.isActivityFinished());
        return dto;
    }
}
