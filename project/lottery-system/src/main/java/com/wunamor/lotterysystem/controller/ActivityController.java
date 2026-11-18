package com.wunamor.lotterysystem.controller;

import com.wunamor.lotterysystem.common.pojo.PageResult;
import com.wunamor.lotterysystem.controller.request.ActivityCreateRequest;
import com.wunamor.lotterysystem.controller.request.ActivityDetailRequest;
import com.wunamor.lotterysystem.controller.request.ActivityListRequest;
import com.wunamor.lotterysystem.controller.request.ActivityPrizeRequest;
import com.wunamor.lotterysystem.controller.request.ActivityUserRequest;
import com.wunamor.lotterysystem.controller.response.ActivityCreateResponse;
import com.wunamor.lotterysystem.controller.response.ActivityDetailPrizeResponse;
import com.wunamor.lotterysystem.controller.response.ActivityDetailResponse;
import com.wunamor.lotterysystem.controller.response.ActivityDetailUserResponse;
import com.wunamor.lotterysystem.controller.response.ActivityListResponse;
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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动接口：创建活动、活动列表和活动抽奖详情，仅管理员可访问。
 */
@Slf4j
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 创建活动：活动基本信息、圈选奖品、圈选人员一起提交。
     */
    @PostMapping("/create")
    public ActivityCreateResponse createActivity(@Valid @RequestBody ActivityCreateRequest request) {
        // 1. 记录请求关键信息，便于排查问题
        log.info("创建活动，activityName: {}, prizeCount: {}, userCount: {}",
                request.getActivityName(), sizeOf(request.getActivityPrizeList()),
                sizeOf(request.getActivityUserList()));

        // 2. 请求对象转 Service 命令，调用创建活动
        ActivityCreateDTO dto = activityService.createActivity(convertToCreateCommand(request));

        // 3. DTO 转响应，只暴露活动 id
        ActivityCreateResponse response = new ActivityCreateResponse();
        response.setActivityId(dto.getActivityId());
        return response;
    }

    /**
     * 分页查询活动列表，按 id 倒序；仅管理员可访问。
     */
    @GetMapping("/list")
    public PageResult<ActivityListResponse> listActivities(
            @Valid @ModelAttribute ActivityListRequest request) {
        // 1. 记录分页参数
        log.info("查询活动列表，currentPage: {}, pageSize: {}",
                request.getCurrentPage(), request.getPageSize());

        // 2. 请求对象转 Service 命令
        ActivityListCommand command = new ActivityListCommand();
        BeanUtils.copyProperties(request, command);

        // 3. 调用 Service 查询分页数据
        PageResult<ActivityListDTO> result = activityService.listActivities(command);

        // 4. DTO 转响应，保留分页信息
        List<ActivityListResponse> records = result.getRecords().stream()
                .map(this::convertToActivityListResponse)
                .collect(Collectors.toList());

        return new PageResult<>(records, result.getTotal(), result.getCurrentPage(),
                result.getPageSize());
    }

    /**
     * 查询活动抽奖详情；缓存优先，未命中时由 Service 回源数据库。
     */
    @GetMapping("/detail")
    public ActivityDetailResponse getActivityDetail(
            @Valid @ModelAttribute ActivityDetailRequest request) {
        // 1. 记录查询参数，便于排查缓存和数据库问题
        log.info("查询活动详情，activityId: {}", request.getActivityId());

        // 2. 调用 Service 查询详情
        ActivityDetailDTO detail = activityService.getActivityDetail(request.getActivityId());

        // 3. DTO 转响应，嵌套列表逐项转换
        return convertToActivityDetailResponse(detail);
    }

    /**
     * 请求对象转 Service 命令；嵌套列表逐项转换，避免 BeanUtils 浅拷贝导致泛型不匹配。
     */
    private ActivityCreateCommand convertToCreateCommand(ActivityCreateRequest request) {
        // 基础字段直接拷贝，嵌套列表逐项转换，避免 BeanUtils 浅拷贝导致泛型不匹配
        ActivityCreateCommand command = new ActivityCreateCommand();
        command.setActivityName(request.getActivityName());
        command.setDescription(request.getDescription());
        command.setActivityPrizeList(request.getActivityPrizeList().stream()
                .map(this::convertToPrizeCommand)
                .collect(Collectors.toList()));
        command.setActivityUserList(request.getActivityUserList().stream()
                .map(this::convertToUserCommand)
                .collect(Collectors.toList()));
        return command;
    }

    private ActivityPrizeCommand convertToPrizeCommand(ActivityPrizeRequest request) {
        ActivityPrizeCommand command = new ActivityPrizeCommand();
        command.setPrizeId(request.getPrizeId());
        command.setPrizeAmount(request.getPrizeAmount());
        command.setPrizeTier(request.getPrizeTier());
        return command;
    }

    private ActivityUserCommand convertToUserCommand(ActivityUserRequest request) {
        ActivityUserCommand command = new ActivityUserCommand();
        command.setUserId(request.getUserId());
        return command;
    }

    private ActivityListResponse convertToActivityListResponse(ActivityListDTO dto) {
        ActivityListResponse response = new ActivityListResponse();
        BeanUtils.copyProperties(dto, response);
        return response;
    }

    /**
     * 活动详情 DTO 转响应，嵌套奖品和人员逐个转换。
     */
    private ActivityDetailResponse convertToActivityDetailResponse(ActivityDetailDTO dto) {
        // 1. 基础字段直接赋值，避免 BeanUtils 浅拷贝泛型列表
        ActivityDetailResponse response = new ActivityDetailResponse();
        response.setActivityId(dto.getActivityId());
        response.setActivityName(dto.getActivityName());
        response.setDescription(dto.getDescription());
        response.setValid(dto.isValid());

        // 2. 奖品和人员列表逐项转换，缓存快照异常为 null 时按空列表返回
        response.setPrizes(dto.getPrizes() == null ? List.of()
                : dto.getPrizes().stream()
                        .map(this::convertToActivityDetailPrizeResponse)
                        .collect(Collectors.toList()));
        response.setUsers(dto.getUsers() == null ? List.of()
                : dto.getUsers().stream()
                        .map(this::convertToActivityDetailUserResponse)
                        .collect(Collectors.toList()));
        return response;
    }

    private ActivityDetailPrizeResponse convertToActivityDetailPrizeResponse(
            ActivityDetailPrizeDTO dto) {
        // 1. 奖品基础字段和状态直接赋值
        ActivityDetailPrizeResponse response = new ActivityDetailPrizeResponse();
        response.setPrizeId(dto.getPrizeId());
        response.setName(dto.getName());
        response.setDescription(dto.getDescription());
        response.setPrice(dto.getPrice());
        response.setImageUrl(dto.getImageUrl());
        response.setPrizeAmount(dto.getPrizeAmount());
        response.setPrizeTier(dto.getPrizeTier());
        response.setPrizeTierName(dto.getPrizeTierName());
        response.setValid(dto.isValid());

        // 2. 中奖名单为空时返回空列表，保持前端可直接遍历
        response.setList(dto.getList() == null ? List.of()
                : dto.getList().stream()
                        .map(this::convertToActivityDetailUserResponse)
                        .collect(Collectors.toList()));
        return response;
    }

    private ActivityDetailUserResponse convertToActivityDetailUserResponse(
            ActivityDetailUserDTO dto) {
        ActivityDetailUserResponse response = new ActivityDetailUserResponse();
        response.setUserId(dto.getUserId());
        response.setUserName(dto.getUserName());
        return response;
    }

    private int sizeOf(List<?> list) {
        // 日志里只关心数量，null 按 0 处理
        return list == null ? 0 : list.size();
    }
}
