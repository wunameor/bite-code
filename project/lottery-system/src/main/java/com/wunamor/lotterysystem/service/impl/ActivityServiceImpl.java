package com.wunamor.lotterysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wunamor.lotterysystem.common.enums.ActivityStatusEnum;
import com.wunamor.lotterysystem.common.errorcode.ErrorCode;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.pojo.PageResult;
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
import com.wunamor.lotterysystem.service.ActivityCacheService;
import com.wunamor.lotterysystem.service.ActivityService;
import com.wunamor.lotterysystem.service.command.ActivityCreateCommand;
import com.wunamor.lotterysystem.service.command.ActivityListCommand;
import com.wunamor.lotterysystem.service.command.ActivityPrizeCommand;
import com.wunamor.lotterysystem.service.command.ActivityUserCommand;
import com.wunamor.lotterysystem.service.dto.ActivityCreateDTO;
import com.wunamor.lotterysystem.service.dto.ActivityDetailDTO;
import com.wunamor.lotterysystem.service.dto.ActivityDetailPrizeDTO;
import com.wunamor.lotterysystem.service.dto.ActivityDetailUserDTO;
import com.wunamor.lotterysystem.service.dto.ActivityListDTO;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.wunamor.lotterysystem.service.enums.ActivityUserStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动服务实现。
 */
@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

    private static final int MAX_TEXT_LENGTH = 255;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final ActivityMapper activityMapper;
    private final ActivityPrizeMapper activityPrizeMapper;
    private final ActivityUserMapper activityUserMapper;
    private final PrizeMapper prizeMapper;
    private final UserMapper userMapper;
    private final ActivityCacheService activityCacheService;

    public ActivityServiceImpl(ActivityMapper activityMapper,
                               ActivityPrizeMapper activityPrizeMapper,
                               ActivityUserMapper activityUserMapper,
                               PrizeMapper prizeMapper,
                               UserMapper userMapper,
                               ActivityCacheService activityCacheService) {
        this.activityMapper = activityMapper;
        this.activityPrizeMapper = activityPrizeMapper;
        this.activityUserMapper = activityUserMapper;
        this.prizeMapper = prizeMapper;
        this.userMapper = userMapper;
        this.activityCacheService = activityCacheService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivityCreateDTO createActivity(ActivityCreateCommand command) {
        // 1. 校验活动基础信息、奖品列表、人员列表
        checkActivityInfo(command);

        // 2. 取出奖品/人员 id，并检查同一活动内是否重复
        List<ActivityPrizeCommand> prizeCommands = command.getActivityPrizeList();
        List<ActivityUserCommand> userCommands = command.getActivityUserList();
        List<Long> prizeIds = prizeCommands.stream().map(ActivityPrizeCommand::getPrizeId).toList();
        List<Long> userIds = userCommands.stream().map(ActivityUserCommand::getUserId).toList();
        checkDuplicateIds(prizeIds, ServiceErrorCodeConstants.ACTIVITY_PRIZE_INVALID);
        checkDuplicateIds(userIds, ServiceErrorCodeConstants.ACTIVITY_USER_INVALID);

        // 3. 校验奖品和人员是否真实存在，并保留查询结果供缓存快照复用
        List<PrizeDO> prizes = queryPrizes(prizeIds);
        List<UserDO> users = queryUsers(userIds);

        // 4. 校验选中人数是否覆盖奖品总数量
        checkUserCount(prizeCommands, userCommands);

        // 5. 同一事务写入活动主表、活动奖品、活动人员
        ActivityDO activityDO = buildActivityDO(command);
        try {
            activityMapper.insert(activityDO);
            saveActivityPrizes(activityDO.getId(), prizeCommands);
            saveActivityUsers(activityDO.getId(), userCommands, users);
        } catch (RuntimeException e) {
            log.error("活动落库失败，activityName: {}", command.getActivityName(), e);
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_CREATE_ERROR);
        }

        // 6. 事务提交后写入活动详情快照，后续抽奖页面从 Redis 读取
        ActivityDetailDTO detail = buildActivityDetail(activityDO, prizeCommands, prizes,
                userCommands, users);
        cacheAfterCommit(detail);

        // 7. 返回新活动 id
        ActivityCreateDTO dto = new ActivityCreateDTO();
        dto.setActivityId(activityDO.getId());
        return dto;
    }

    @Override
    public PageResult<ActivityListDTO> listActivities(ActivityListCommand command) {
        // 1. 构建分页参数
        Page<ActivityDO> page = buildPage(command);

        // 2. 按 id 倒序查询活动分页数据
        Page<ActivityDO> result = activityMapper.selectPage(page,
                new LambdaQueryWrapper<ActivityDO>().orderByDesc(ActivityDO::getId));

        // 3. DO 转 DTO，并计算是否进行中
        List<ActivityListDTO> records = result.getRecords().stream()
                .map(this::convertToActivityListDTO)
                .collect(Collectors.toList());

        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    /**
     * 校验活动基本信息、奖品列表、人员列表。
     */
    private void checkActivityInfo(ActivityCreateCommand command) {
        // 命令对象为空时无法继续校验
        if (command == null) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_INFO_IS_EMPTY);
        }
        // 活动名称和描述必填，且不能超过数据库字段长度
        if (!StringUtils.hasText(command.getActivityName())) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_NAME_IS_EMPTY);
        }
        if (command.getActivityName().length() > MAX_TEXT_LENGTH) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_NAME_TOO_LONG);
        }
        if (!StringUtils.hasText(command.getDescription())) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_DESCRIPTION_IS_EMPTY);
        }
        if (command.getDescription().length() > MAX_TEXT_LENGTH) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_DESCRIPTION_TOO_LONG);
        }
        // 奖品和人员列表都不能为空
        if (command.getActivityPrizeList() == null || command.getActivityPrizeList().isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_LIST_IS_EMPTY);
        }
        if (command.getActivityUserList() == null || command.getActivityUserList().isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_USER_LIST_IS_EMPTY);
        }
        // 单条奖品/人员记录也要校验，防止绕过 Controller 直接调用 Service
        checkPrizeCommands(command.getActivityPrizeList());
        checkUserCommands(command.getActivityUserList());
    }

    /**
     * 单个奖品命令的字段校验。
     */
    private void checkPrizeCommands(List<ActivityPrizeCommand> prizes) {
        // 奖品 id、数量、等级缺一不可，数量必须大于 0
        for (ActivityPrizeCommand prize : prizes) {
            if (prize == null || prize.getPrizeId() == null
                    || prize.getPrizeAmount() == null || prize.getPrizeAmount() < 1
                    || prize.getPrizeTier() == null) {
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_INVALID);
            }
        }
    }

    /**
     * 单个人员命令的字段校验。
     */
    private void checkUserCommands(List<ActivityUserCommand> users) {
        // 人员 id 不能为空
        for (ActivityUserCommand user : users) {
            if (user == null || user.getUserId() == null) {
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_USER_INVALID);
            }
        }
    }

    /**
     * 检查 id 列表是否有重复值。
     */
    private void checkDuplicateIds(List<Long> ids, ErrorCode errorCode) {
        // Set 去重后长度变小，说明原列表有重复 id
        if (new HashSet<>(ids).size() != ids.size()) {
            throw new ServiceException(errorCode);
        }
    }

    /**
     * 查询并校验奖品是否全部存在；返回值供活动详情快照复用。
     */
    private List<PrizeDO> queryPrizes(List<Long> prizeIds) {
        // 查出来的数量少于请求数量，说明有奖品 id 不存在
        List<PrizeDO> prizes = prizeMapper.selectBatchIds(prizeIds);
        if (prizes.size() != prizeIds.size()) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_INVALID);
        }
        return prizes;
    }

    /**
     * 只查 id 和用户名，避免把密码、手机号等无关字段带出来。
     */
    private List<UserDO> queryUsers(List<Long> userIds) {
        // 只查 id 和用户名，避免把密码、手机号等无关字段带出来
        List<UserDO> users = userMapper.listUsersByIds(userIds);
        // 查出来的数量少于请求数量，说明有人员 id 不存在
        if (users.size() != userIds.size()) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_USER_INVALID);
        }
        return users;
    }

    /**
     * 选中人数必须大于等于奖品总数量，否则抽奖时会出现人不敷出。
     */
    private void checkUserCount(List<ActivityPrizeCommand> prizes, List<ActivityUserCommand> users) {
        // 奖品总数量不能超过参与人数，否则抽奖时会出现人不敷出
        long totalPrizeAmount = prizes.stream()
                .mapToLong(ActivityPrizeCommand::getPrizeAmount)
                .sum();
        if (totalPrizeAmount > users.size()) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_USER_NOT_ENOUGH);
        }
    }

    /**
     * 构建活动数据库对象，创建后默认进行中。
     */
    private ActivityDO buildActivityDO(ActivityCreateCommand command) {
        // 新建活动默认进行中
        ActivityDO activityDO = new ActivityDO();
        activityDO.setActivityName(command.getActivityName());
        activityDO.setDescription(command.getDescription());
        activityDO.setStatus(ActivityStatusEnum.RUNNING.getCode());
        return activityDO;
    }

    /**
     * 批量写入活动奖品关联，初始状态为待抽取。
     */
    private void saveActivityPrizes(Long activityId, List<ActivityPrizeCommand> prizes) {
        // 1. 逐个转换为数据库对象，统一设置活动 id 和待抽取状态
        List<ActivityPrizeDO> rows = new ArrayList<>(prizes.size());
        for (ActivityPrizeCommand prize : prizes) {
            ActivityPrizeDO row = new ActivityPrizeDO();
            row.setActivityId(activityId);
            row.setPrizeId(prize.getPrizeId());
            row.setPrizeAmount(prize.getPrizeAmount());
            row.setPrizeTiers(prize.getPrizeTier().getCode());
            row.setStatus(ActivityPrizeStatusEnum.PENDING.getCode());
            rows.add(row);
        }
        // 2. 一次批量写入，减少数据库往返
        if (!rows.isEmpty()) {
            activityPrizeMapper.insertBatch(rows);
        }
    }

    /**
     * 批量写入活动人员关联，用户名以数据库查询结果为准。
     */
    private void saveActivityUsers(Long activityId, List<ActivityUserCommand> userCommands,
                                   List<UserDO> users) {
        // 1. 先建立 userId -> userName 映射，用户名以数据库查询结果为准
        Map<Long, String> userNameById = users.stream()
                .collect(Collectors.toMap(UserDO::getId, UserDO::getUserName));
        // 2. 逐个转换为数据库对象，统一设置活动 id 和待中奖状态
        List<ActivityUserDO> rows = new ArrayList<>(userCommands.size());
        for (ActivityUserCommand userCommand : userCommands) {
            ActivityUserDO row = new ActivityUserDO();
            row.setActivityId(activityId);
            row.setUserId(userCommand.getUserId());
            row.setUserName(userNameById.get(userCommand.getUserId()));
            row.setStatus(ActivityUserStatusEnum.PENDING.getCode());
            rows.add(row);
        }
        // 3. 一次批量写入，减少数据库往返
        if (!rows.isEmpty()) {
            activityUserMapper.insertBatch(rows);
        }
    }

    /**
     * 构建活动详情快照，字段与抽奖页面 /activity-detail/find 的返回结构保持一致。
     */
    private ActivityDetailDTO buildActivityDetail(ActivityDO activityDO,
                                                  List<ActivityPrizeCommand> prizeCommands,
                                                  List<PrizeDO> prizes,
                                                  List<ActivityUserCommand> userCommands,
                                                  List<UserDO> users) {
        // 1. 填充活动基础信息
        ActivityDetailDTO detail = new ActivityDetailDTO();
        detail.setActivityId(activityDO.getId());
        detail.setActivityName(activityDO.getActivityName());
        detail.setDescription(activityDO.getDescription());
        detail.setValid(ActivityStatusEnum.RUNNING.getCode().equals(activityDO.getStatus()));

        // 2. 按 prizeId 映射奖品，再按命令顺序组装奖品快照
        Map<Long, PrizeDO> prizeById = prizes.stream()
                .collect(Collectors.toMap(PrizeDO::getId, prize -> prize));
        List<ActivityDetailPrizeDTO> prizeDetails = new ArrayList<>();
        for (ActivityPrizeCommand prizeCommand : prizeCommands) {
            PrizeDO prizeDO = prizeById.get(prizeCommand.getPrizeId());
            if (prizeDO != null) {
                prizeDetails.add(buildActivityPrizeDetail(prizeCommand, prizeDO));
            }
        }
        detail.setPrizes(prizeDetails);

        // 3. 按命令顺序组装人员快照，用户名使用数据库查询结果
        Map<Long, String> userNameById = users.stream()
                .collect(Collectors.toMap(UserDO::getId, UserDO::getUserName));
        List<ActivityDetailUserDTO> userDetails = new ArrayList<>();
        for (ActivityUserCommand userCommand : userCommands) {
            ActivityDetailUserDTO userDetail = new ActivityDetailUserDTO();
            userDetail.setUserId(userCommand.getUserId());
            userDetail.setUserName(userNameById.get(userCommand.getUserId()));
            userDetails.add(userDetail);
        }
        detail.setUsers(userDetails);
        return detail;
    }

    /**
     * 单个奖品快照：创建时奖品均为待抽取，中奖名单为空。
     */
    private ActivityDetailPrizeDTO buildActivityPrizeDetail(ActivityPrizeCommand prizeCommand,
                                                            PrizeDO prizeDO) {
        ActivityDetailPrizeDTO detail = new ActivityDetailPrizeDTO();
        detail.setPrizeId(prizeDO.getId());
        detail.setName(prizeDO.getName());
        detail.setDescription(prizeDO.getDescription());
        detail.setPrice(prizeDO.getPrice());
        detail.setImageUrl(prizeDO.getImageUrl());
        detail.setPrizeAmount(prizeCommand.getPrizeAmount());
        detail.setPrizeTier(prizeCommand.getPrizeTier().getCode());
        detail.setPrizeTierName(prizeCommand.getPrizeTier().getDesc());
        detail.setValid(true);
        detail.setList(List.of());
        return detail;
    }

    /**
     * 事务提交后写入活动详情快照；无事务时直接写入，方便单元测试和直接调用。
     */
    private void cacheAfterCommit(ActivityDetailDTO detail) {
        // 1. 没有事务时直接写缓存
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            activityCacheService.cacheActivityDetail(detail);
            return;
        }

        // 2. 有事务时等提交成功后再写，避免回滚后 Redis 留下不存在的活动
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                activityCacheService.cacheActivityDetail(detail);
            }
        });
    }

    /**
     * 构建分页参数；Service 被直接调用时也做一次兜底。
     */
    private Page<ActivityDO> buildPage(ActivityListCommand command) {
        // Service 被直接调用时也做一次兜底，避免页码/每页条数越界
        long currentPage = command == null ? 1 : Math.max(1, command.getCurrentPage());
        long pageSize = command == null ? DEFAULT_PAGE_SIZE
                : Math.min(Math.max(1, command.getPageSize()), MAX_PAGE_SIZE);
        return new Page<>(currentPage, pageSize);
    }

    /**
     * 把活动数据库对象转成 Service 层列表项。
     */
    private ActivityListDTO convertToActivityListDTO(ActivityDO activityDO) {
        ActivityListDTO dto = new ActivityListDTO();
        dto.setActivityId(activityDO.getId());
        dto.setActivityName(activityDO.getActivityName());
        dto.setDescription(activityDO.getDescription());
        // 只有进行中的活动才允许去抽奖
        dto.setValid(ActivityStatusEnum.RUNNING.getCode().equals(activityDO.getStatus()));
        return dto;
    }
}
