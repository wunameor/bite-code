package com.wunamor.lotterysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wunamor.lotterysystem.common.config.DrawCompensationProperties;
import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.mapper.ActivityPrizeMapper;
import com.wunamor.lotterysystem.mapper.ActivityUserMapper;
import com.wunamor.lotterysystem.mapper.DrawRequestMapper;
import com.wunamor.lotterysystem.mapper.WinningRecordMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import com.wunamor.lotterysystem.mapper.dataobject.DrawRequestDO;
import com.wunamor.lotterysystem.service.DrawPrizeService;
import com.wunamor.lotterysystem.service.DrawRequestService;
import com.wunamor.lotterysystem.service.command.DrawPrizeCommand;
import com.wunamor.lotterysystem.service.dto.DrawRequestDTO;
import com.wunamor.lotterysystem.service.dto.DrawStatusDTO;
import com.wunamor.lotterysystem.service.dto.DrawWinnerDTO;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.wunamor.lotterysystem.service.enums.DrawRequestStatusEnum;
import com.wunamor.lotterysystem.service.mq.message.DrawCommandMessage;
import com.wunamor.lotterysystem.service.mq.producer.DrawMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
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
 * 抽奖异步任务服务实现。
 */
@Slf4j
@Service
public class DrawRequestServiceImpl implements DrawRequestService {

    private final DrawRequestMapper drawRequestMapper;
    private final DrawMessageProducer drawMessageProducer;
    private final DrawPrizeService drawPrizeService;
    private final ActivityPrizeMapper activityPrizeMapper;
    private final ActivityUserMapper activityUserMapper;
    private final WinningRecordMapper winningRecordMapper;
    private final DrawCompensationProperties compensationProperties;

    public DrawRequestServiceImpl(DrawRequestMapper drawRequestMapper,
                                  DrawMessageProducer drawMessageProducer,
                                  DrawPrizeService drawPrizeService,
                                  ActivityPrizeMapper activityPrizeMapper,
                                  ActivityUserMapper activityUserMapper,
                                  WinningRecordMapper winningRecordMapper,
                                  DrawCompensationProperties compensationProperties) {
        this.drawRequestMapper = drawRequestMapper;
        this.drawMessageProducer = drawMessageProducer;
        this.drawPrizeService = drawPrizeService;
        this.activityPrizeMapper = activityPrizeMapper;
        this.activityUserMapper = activityUserMapper;
        this.winningRecordMapper = winningRecordMapper;
        this.compensationProperties = compensationProperties;
    }

    @Override
    public DrawRequestDTO submit(DrawPrizeCommand command) {
        // 1. 这里只做格式校验，最终抽奖资格和名额仍由消费者在事务内校验
        validateSubmitCommand(command);

        // 2. 同一奖品允许多次抽奖，但已中奖和未完成任务的人数不能超过奖品名额
        checkPrizeCapacity(command);

        // 3. 先落任务，再投递消息；投递失败时任务保持 PENDING 等待补偿
        DrawRequestDO request = buildRequestDO(command);
        drawRequestMapper.insert(request);
        publishQuietly(request);

        // 4. 返回 requestId 和初始状态
        DrawRequestDTO dto = new DrawRequestDTO();
        dto.setRequestId(request.getId());
        dto.setActivityId(request.getActivityId());
        dto.setPrizeId(request.getPrizeId());
        dto.setStatus(request.getStatus());
        return dto;
    }

    @Override
    public DrawStatusDTO getStatus(Long requestId) {
        // 1. 查询任务，不存在时返回业务错误
        DrawRequestDO request = getRequest(requestId);

        // 2. 填充基础状态和错误信息
        DrawStatusDTO dto = new DrawStatusDTO();
        dto.setRequestId(request.getId());
        dto.setActivityId(request.getActivityId());
        dto.setPrizeId(request.getPrizeId());
        dto.setStatus(request.getStatus());
        dto.setErrorCode(request.getErrorCode());
        dto.setErrorMessage(request.getErrorMessage());

        // 3. 填充奖品等级、剩余名额和中奖人员
        ActivityPrizeDO activityPrize = findActivityPrize(
                request.getActivityId(), request.getPrizeId());
        fillPrizeTier(dto, activityPrize);
        fillPrizeQuota(dto, activityPrize);
        dto.setWinnerList(buildWinnerList(request));

        // 4. 填充活动是否结束和下一个待抽奖品
        boolean finished = isActivityFinished(request.getActivityId());
        dto.setActivityFinished(finished);
        if (!finished && DrawRequestStatusEnum.SUCCESS.getCode().equals(request.getStatus())) {
            fillNextPrize(dto, request.getActivityId());
        }
        return dto;
    }

    @Override
    public void handleCommand(DrawCommandMessage message) {
        // 1. 抢占任务；重复消息或已结束任务直接确认
        Long requestId = message.getRequestId();
        if (!markProcessing(requestId)) {
            log.info("抽奖任务已被处理或状态已变化，requestId: {}", requestId);
            return;
        }

        // 2. 执行抽奖事务，按异常类型决定写回失败还是交给 MQ 重试
        try {
            drawPrizeService.drawPrize(buildDrawCommand(message));
            log.info("抽奖任务处理成功，requestId: {}", requestId);
        } catch (ServiceException e) {
            handleServiceException(requestId, e);
        } catch (RuntimeException e) {
            log.error("抽奖任务处理异常，requestId: {}", requestId, e);
            // 交给 Spring AMQP 重试；重试用尽后由容器拒绝并进入死信队列
            throw e;
        }
    }

    @Override
    public boolean markProcessing(Long requestId) {
        if (requestId == null) {
            return false;
        }

        // PENDING 正常抢占为 PROCESSING
        int updated = drawRequestMapper.update(null,
                new LambdaUpdateWrapper<DrawRequestDO>()
                        .eq(DrawRequestDO::getId, requestId)
                        .eq(DrawRequestDO::getStatus, DrawRequestStatusEnum.PENDING.getCode())
                        .set(DrawRequestDO::getStatus,
                                DrawRequestStatusEnum.PROCESSING.getCode()));
        if (updated == 1) {
            return true;
        }

        // Spring AMQP 重试或重复消息到达时允许继续处理；
        // 真正的幂等由活动行锁、奖品状态和任务终态共同保证。
        DrawRequestDO request = drawRequestMapper.selectById(requestId);
        return request != null && DrawRequestStatusEnum.PROCESSING.getCode()
                .equals(request.getStatus());
    }

    @Override
    public void markFailed(Long requestId, int errorCode, String errorMessage) {
        if (requestId == null) {
            return;
        }

        // 已经成功的任务不能被失败消息覆盖
        int updated = drawRequestMapper.update(null,
                new LambdaUpdateWrapper<DrawRequestDO>()
                        .eq(DrawRequestDO::getId, requestId)
                        .in(DrawRequestDO::getStatus,
                                DrawRequestStatusEnum.PENDING.getCode(),
                                DrawRequestStatusEnum.PROCESSING.getCode())
                        .set(DrawRequestDO::getStatus, DrawRequestStatusEnum.FAILED.getCode())
                        .set(DrawRequestDO::getErrorCode, errorCode)
                        .set(DrawRequestDO::getErrorMessage, errorMessage));
        if (updated != 1) {
            log.warn("抽奖任务标记失败未生效，requestId: {}, status 可能已变化", requestId);
        }
    }

    /**
     * 区分抽奖业务失败和技术失败；技术异常继续向上抛，保留任务给重试和补偿。
     */
    private void handleServiceException(Long requestId, ServiceException e) {
        // 数据库等技术异常保留 PROCESSING，让死信队列和补偿任务再次处理
        if (e.getCode() == ServiceErrorCodeConstants.DRAW_PRIZE_ERROR.getCode()) {
            log.error("抽奖任务技术异常，requestId: {}", requestId, e);
            throw e;
        }

        // 业务校验失败不会因为重试而成功，直接写回失败原因并确认消息
        log.warn("抽奖任务业务失败，requestId: {}, code: {}, message: {}",
                requestId, e.getCode(), e.getMessage());
        markFailed(requestId, e.getCode(), e.getMessage());
    }

    /**
     * 把消息转换为抽奖服务命令。
     */
    private DrawPrizeCommand buildDrawCommand(DrawCommandMessage message) {
        DrawPrizeCommand command = new DrawPrizeCommand();
        command.setRequestId(message.getRequestId());
        command.setActivityId(message.getActivityId());
        command.setPrizeId(message.getPrizeId());
        command.setWinnerIds(message.getWinnerIds());
        return command;
    }

    /**
     * 定时补偿：处理超时、重投未完成任务、把超过重试上限的任务标记失败。
     */
    @Scheduled(fixedDelayString = "#{@drawCompensationProperties.scanInterval.toMillis()}")
    public void compensate() {
        if (!compensationProperties.isEnabled()) {
            return;
        }

        // 1. 先处理超过重试上限的任务，避免无限重投
        failExhaustedTasks();

        // 2. 重投未消费的 PENDING 和超时 PROCESSING 任务
        republishPendingTasks();
        republishProcessingTasks();
    }

    private void validateSubmitCommand(DrawPrizeCommand command) {
        // 1. 活动、奖品、中奖人员均不能为空
        if (command == null || command.getActivityId() == null
                || command.getPrizeId() == null
                || command.getWinnerIds() == null
                || command.getWinnerIds().isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_INFO_IS_EMPTY);
        }

        // 2. 中奖人员 id 不能为空且不能重复
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
     * 检查奖品剩余名额是否足够本次提交。
     *
     * <p>抽奖改成一次提交一个人后，同一奖品会连续产生多个任务，所以不能再用
     * “同一奖品有未完成任务就拒绝”的规则。这里把已中奖人数、未完成任务人数
     * 和本次人数一起计算，提前拦截明显超额提交；最终名额校验仍在抽奖事务里
     * 通过活动行锁完成。</p>
     *
     * @param command 抽奖命令
     */
    private void checkPrizeCapacity(DrawPrizeCommand command) {
        // 1. 活动奖品不存在时无法判断名额
        ActivityPrizeDO activityPrize = findActivityPrize(
                command.getActivityId(), command.getPrizeId());
        if (activityPrize == null) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_INVALID);
        }

        // 2. 剩余名额 = 奖品数量 - 已中奖人数 - 未完成任务占用的人数
        long prizeAmount = activityPrize.getPrizeAmount() == null
                ? 0L : activityPrize.getPrizeAmount();
        long drawnCount = winningRecordMapper.countByActivityIdAndPrizeId(
                command.getActivityId(), command.getPrizeId());
        long inFlightCount = countUnfinishedWinners(command);

        // 3. 超出剩余名额时直接拒绝，避免产生注定失败的异步任务
        if (drawnCount + inFlightCount + command.getWinnerIds().size() > prizeAmount) {
            throw new ServiceException(
                    ServiceErrorCodeConstants.DRAW_PRIZE_SLOT_NOT_ENOUGH);
        }
    }

    /**
     * 统计同一活动、同一奖品下未完成任务的已占用名额。
     *
     * @param command 抽奖命令
     * @return 未完成任务中的中奖人数
     */
    private long countUnfinishedWinners(DrawPrizeCommand command) {
        List<DrawRequestDO> tasks = drawRequestMapper.selectList(
                new LambdaQueryWrapper<DrawRequestDO>()
                        .eq(DrawRequestDO::getActivityId, command.getActivityId())
                        .eq(DrawRequestDO::getPrizeId, command.getPrizeId())
                        .in(DrawRequestDO::getStatus,
                                DrawRequestStatusEnum.PENDING.getCode(),
                                DrawRequestStatusEnum.PROCESSING.getCode()));

        long total = 0L;
        for (DrawRequestDO task : tasks) {
            total += parseWinnerIds(task.getWinnerIds()).size();
        }
        return total;
    }

    private DrawRequestDO buildRequestDO(DrawPrizeCommand command) {
        DrawRequestDO request = new DrawRequestDO();
        request.setActivityId(command.getActivityId());
        request.setPrizeId(command.getPrizeId());
        request.setWinnerIds(JacksonUtil.writeObjectAsString(command.getWinnerIds()));
        request.setStatus(DrawRequestStatusEnum.PENDING.getCode());
        request.setRetryCount(0);
        return request;
    }

    private DrawRequestDO getRequest(Long requestId) {
        if (requestId == null) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_REQUEST_NOT_FOUND);
        }
        DrawRequestDO request = drawRequestMapper.selectById(requestId);
        if (request == null) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_REQUEST_NOT_FOUND);
        }
        return request;
    }

    private ActivityPrizeDO findActivityPrize(Long activityId, Long prizeId) {
        return activityPrizeMapper.selectOne(new LambdaQueryWrapper<ActivityPrizeDO>()
                .eq(ActivityPrizeDO::getActivityId, activityId)
                .eq(ActivityPrizeDO::getPrizeId, prizeId));
    }

    private void fillPrizeTier(DrawStatusDTO dto, ActivityPrizeDO activityPrize) {
        if (activityPrize == null) {
            return;
        }
        dto.setPrizeTier(activityPrize.getPrizeTiers());
        PrizeTierEnum tier = PrizeTierEnum.getByCode(activityPrize.getPrizeTiers());
        dto.setPrizeTierName(tier == null
                ? activityPrize.getPrizeTiers() : tier.getDesc());
    }

    /**
     * 填充当前奖品的剩余名额和是否已经抽满，供前端决定是否进入下一奖品。
     *
     * @param dto           状态查询结果
     * @param activityPrize 活动奖品
     */
    private void fillPrizeQuota(DrawStatusDTO dto, ActivityPrizeDO activityPrize) {
        if (activityPrize == null) {
            return;
        }
        long prizeAmount = activityPrize.getPrizeAmount() == null
                ? 0L : activityPrize.getPrizeAmount();
        long drawnCount = winningRecordMapper.countByActivityIdAndPrizeId(
                activityPrize.getActivityId(), activityPrize.getPrizeId());
        dto.setPrizeRemaining((int) Math.max(0, prizeAmount - drawnCount));
        dto.setPrizeFinished(drawnCount >= prizeAmount);
    }

    private List<DrawWinnerDTO> buildWinnerList(DrawRequestDO request) {
        // 1. 成功任务优先使用落库的结果快照
        if (StringUtils.hasText(request.getResultJson())) {
            try {
                List<DrawWinnerDTO> winners = JacksonUtil.readListValue(
                        request.getResultJson(), DrawWinnerDTO.class);
                if (winners != null) {
                    return winners;
                }
            } catch (RuntimeException e) {
                log.warn("抽奖结果快照解析失败，requestId: {}", request.getId(), e);
            }
        }

        // 2. 未完成或失败任务回退到请求中的人员 id，并补数据库用户名
        List<Long> winnerIds = parseWinnerIds(request.getWinnerIds());
        if (winnerIds.isEmpty()) {
            return List.of();
        }
        Map<Long, ActivityUserDO> userById = queryActivityUserMap(
                request.getActivityId(), winnerIds);
        List<DrawWinnerDTO> winners = new ArrayList<>(winnerIds.size());
        for (Long winnerId : winnerIds) {
            DrawWinnerDTO winner = new DrawWinnerDTO();
            winner.setUserId(winnerId);
            ActivityUserDO activityUser = userById.get(winnerId);
            winner.setUserName(activityUser == null ? null : activityUser.getUserName());
            winners.add(winner);
        }
        return winners;
    }

    private Map<Long, ActivityUserDO> queryActivityUserMap(Long activityId, List<Long> userIds) {
        List<ActivityUserDO> users = activityUserMapper.selectList(
                new LambdaQueryWrapper<ActivityUserDO>()
                        .eq(ActivityUserDO::getActivityId, activityId)
                        .in(ActivityUserDO::getUserId, userIds));
        return users.stream().collect(Collectors.toMap(
                ActivityUserDO::getUserId, Function.identity(), (left, right) -> left));
    }

    private List<Long> parseWinnerIds(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            List<Long> winnerIds = JacksonUtil.readListValue(json, Long.class);
            return winnerIds == null ? List.of() : winnerIds;
        } catch (RuntimeException e) {
            log.warn("抽奖任务 winnerIds 解析失败", e);
            return List.of();
        }
    }

    private boolean isActivityFinished(Long activityId) {
        Long pendingCount = activityPrizeMapper.selectCount(
                new LambdaQueryWrapper<ActivityPrizeDO>()
                        .eq(ActivityPrizeDO::getActivityId, activityId)
                        .eq(ActivityPrizeDO::getStatus,
                                ActivityPrizeStatusEnum.PENDING.getCode()));
        return pendingCount == null || pendingCount == 0;
    }

    private void fillNextPrize(DrawStatusDTO dto, Long activityId) {
        ActivityPrizeDO next = listOrderedPrizes(activityId).stream()
                .filter(prize -> ActivityPrizeStatusEnum.PENDING.getCode()
                        .equals(prize.getStatus()))
                .findFirst()
                .orElse(null);
        if (next == null) {
            return;
        }
        dto.setNextPrizeId(next.getPrizeId());
        dto.setNextPrizeTier(next.getPrizeTiers());
        PrizeTierEnum tier = PrizeTierEnum.getByCode(next.getPrizeTiers());
        dto.setNextPrizeTierName(tier == null ? next.getPrizeTiers() : tier.getDesc());
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

    private void failExhaustedTasks() {
        Date pendingDeadline = deadline(compensationProperties.getPendingDelay());
        Date processingDeadline = deadline(compensationProperties.getProcessingTimeout());

        // 1. PENDING 超过重试上限后不再投递
        markFailedByRetryLimit(DrawRequestStatusEnum.PENDING.getCode(),
                pendingDeadline, "抽奖任务投递重试次数已用完");

        // 2. PROCESSING 超过重试上限后不再投递
        markFailedByRetryLimit(DrawRequestStatusEnum.PROCESSING.getCode(),
                processingDeadline, "抽奖任务处理超时且重试次数已用完");
    }

    private void markFailedByRetryLimit(String status, Date deadline, String message) {
        drawRequestMapper.update(null, new LambdaUpdateWrapper<DrawRequestDO>()
                .eq(DrawRequestDO::getStatus, status)
                .lt(DrawRequestDO::getGmtModified, deadline)
                .ge(DrawRequestDO::getRetryCount, compensationProperties.getMaxAttempts())
                .set(DrawRequestDO::getStatus, DrawRequestStatusEnum.FAILED.getCode())
                .set(DrawRequestDO::getErrorCode,
                        ServiceErrorCodeConstants.DRAW_PRIZE_ERROR.getCode())
                .set(DrawRequestDO::getErrorMessage, message));
    }

    private void republishPendingTasks() {
        Date deadline = deadline(compensationProperties.getPendingDelay());
        List<DrawRequestDO> tasks = drawRequestMapper.selectList(
                new LambdaQueryWrapper<DrawRequestDO>()
                        .eq(DrawRequestDO::getStatus, DrawRequestStatusEnum.PENDING.getCode())
                        .lt(DrawRequestDO::getGmtModified, deadline)
                        .lt(DrawRequestDO::getRetryCount,
                                compensationProperties.getMaxAttempts())
                        .orderByAsc(DrawRequestDO::getId)
                        .last("LIMIT 20"));
        for (DrawRequestDO task : tasks) {
            if (claimPendingRetry(task)) {
                publishQuietly(task);
            }
        }
    }

    /**
     * 多实例补偿时通过 retry_count 做乐观锁，避免同一任务被重复投递。
     */
    private boolean claimPendingRetry(DrawRequestDO task) {
        return drawRequestMapper.update(null,
                new LambdaUpdateWrapper<DrawRequestDO>()
                        .eq(DrawRequestDO::getId, task.getId())
                        .eq(DrawRequestDO::getStatus, DrawRequestStatusEnum.PENDING.getCode())
                        .eq(DrawRequestDO::getRetryCount, task.getRetryCount())
                        .lt(DrawRequestDO::getRetryCount,
                                compensationProperties.getMaxAttempts())
                        .setSql("retry_count = retry_count + 1")) == 1;
    }

    private void republishProcessingTasks() {
        Date deadline = deadline(compensationProperties.getProcessingTimeout());
        List<DrawRequestDO> tasks = drawRequestMapper.selectList(
                new LambdaQueryWrapper<DrawRequestDO>()
                        .eq(DrawRequestDO::getStatus, DrawRequestStatusEnum.PROCESSING.getCode())
                        .lt(DrawRequestDO::getGmtModified, deadline)
                        .lt(DrawRequestDO::getRetryCount,
                                compensationProperties.getMaxAttempts())
                        .orderByAsc(DrawRequestDO::getId)
                        .last("LIMIT 20"));
        for (DrawRequestDO task : tasks) {
            if (resetProcessingRetry(task)) {
                publishQuietly(task);
            }
        }
    }

    private boolean resetProcessingRetry(DrawRequestDO task) {
        return drawRequestMapper.update(null,
                new LambdaUpdateWrapper<DrawRequestDO>()
                        .eq(DrawRequestDO::getId, task.getId())
                        .eq(DrawRequestDO::getStatus,
                                DrawRequestStatusEnum.PROCESSING.getCode())
                        .eq(DrawRequestDO::getRetryCount, task.getRetryCount())
                        .set(DrawRequestDO::getStatus,
                                DrawRequestStatusEnum.PENDING.getCode())
                        .setSql("retry_count = retry_count + 1")) == 1;
    }

    private void publishQuietly(DrawRequestDO request) {
        try {
            DrawCommandMessage message = buildCommandMessage(request);
            if (message != null) {
                drawMessageProducer.sendCommand(message);
            }
        } catch (RuntimeException e) {
            log.error("抽奖命令投递失败，requestId: {}", request.getId(), e);
        }
    }

    private DrawCommandMessage buildCommandMessage(DrawRequestDO request) {
        List<Long> winnerIds = parseWinnerIds(request.getWinnerIds());
        if (winnerIds.isEmpty()) {
            log.warn("抽奖任务缺少中奖人员，requestId: {}", request.getId());
            return null;
        }
        DrawCommandMessage message = new DrawCommandMessage();
        message.setRequestId(request.getId());
        message.setActivityId(request.getActivityId());
        message.setPrizeId(request.getPrizeId());
        message.setWinnerIds(winnerIds);
        return message;
    }

    private Date deadline(Duration duration) {
        return new Date(System.currentTimeMillis() - duration.toMillis());
    }
}
