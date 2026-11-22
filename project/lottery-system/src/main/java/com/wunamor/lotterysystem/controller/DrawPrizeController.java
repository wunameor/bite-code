package com.wunamor.lotterysystem.controller;

import com.wunamor.lotterysystem.controller.request.DrawPrizeRequest;
import com.wunamor.lotterysystem.controller.request.DrawWinnerRequest;
import com.wunamor.lotterysystem.controller.response.DrawPrizeResponse;
import com.wunamor.lotterysystem.controller.response.DrawStatusResponse;
import com.wunamor.lotterysystem.controller.response.DrawWinnerResponse;
import com.wunamor.lotterysystem.service.DrawRequestService;
import com.wunamor.lotterysystem.service.command.DrawPrizeCommand;
import com.wunamor.lotterysystem.service.dto.DrawRequestDTO;
import com.wunamor.lotterysystem.service.dto.DrawStatusDTO;
import com.wunamor.lotterysystem.service.dto.DrawWinnerDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽奖接口，仅管理员可访问。
 */
@Slf4j
@RestController
@RequestMapping("/draw-prize")
public class DrawPrizeController {

    private final DrawRequestService drawRequestService;

    public DrawPrizeController(DrawRequestService drawRequestService) {
        this.drawRequestService = drawRequestService;
    }

    /**
     * 提交抽奖任务；真正的抽奖由 RabbitMQ 消费者异步执行。
     */
    @PostMapping
    public DrawPrizeResponse drawPrize(@Valid @RequestBody DrawPrizeRequest request) {
        // 1. 记录请求关键信息，便于排查抽奖问题
        log.info("抽奖请求，activityId: {}, prizeId: {}, winnerCount: {}",
                request.getActivityId(), request.getPrizeId(),
                sizeOf(request.getWinnerList()));

        // 2. 创建异步任务，返回 requestId
        DrawRequestDTO result = drawRequestService.submit(convertToCommand(request));

        // 3. DTO 转响应
        return convertToResponse(result);
    }

    /**
     * 查询抽奖任务状态，前端轮询该接口获取最终中奖人员。
     */
    @GetMapping("/status")
    public DrawStatusResponse getStatus(@RequestParam("requestId") Long requestId) {
        DrawStatusDTO result = drawRequestService.getStatus(requestId);
        return convertToStatusResponse(result);
    }

    /**
     * 请求中的中奖人员只取 userId，用户名以数据库快照为准。
     */
    private DrawPrizeCommand convertToCommand(DrawPrizeRequest request) {
        List<Long> winnerIds = request.getWinnerList().stream()
                .map(DrawWinnerRequest::getUserId)
                .collect(Collectors.toList());

        DrawPrizeCommand command = new DrawPrizeCommand();
        command.setActivityId(request.getActivityId());
        command.setPrizeId(request.getPrizeId());
        command.setWinnerIds(winnerIds);
        return command;
    }

    private DrawPrizeResponse convertToResponse(DrawRequestDTO result) {
        DrawPrizeResponse response = new DrawPrizeResponse();
        response.setRequestId(result.getRequestId());
        response.setActivityId(result.getActivityId());
        response.setPrizeId(result.getPrizeId());
        response.setStatus(result.getStatus());
        return response;
    }

    private DrawStatusResponse convertToStatusResponse(DrawStatusDTO result) {
        DrawStatusResponse response = new DrawStatusResponse();
        response.setRequestId(result.getRequestId());
        response.setActivityId(result.getActivityId());
        response.setPrizeId(result.getPrizeId());
        response.setStatus(result.getStatus());
        response.setPrizeTier(result.getPrizeTier());
        response.setPrizeTierName(result.getPrizeTierName());
        response.setWinnerList(convertWinners(result.getWinnerList()));
        response.setActivityFinished(result.isActivityFinished());
        response.setNextPrizeId(result.getNextPrizeId());
        response.setNextPrizeTier(result.getNextPrizeTier());
        response.setNextPrizeTierName(result.getNextPrizeTierName());
        response.setErrorCode(result.getErrorCode());
        response.setErrorMessage(result.getErrorMessage());
        return response;
    }

    private List<DrawWinnerResponse> convertWinners(List<DrawWinnerDTO> winners) {
        if (winners == null) {
            return List.of();
        }
        return winners.stream().map(this::convertWinner).collect(Collectors.toList());
    }

    private DrawWinnerResponse convertWinner(DrawWinnerDTO winner) {
        DrawWinnerResponse response = new DrawWinnerResponse();
        response.setUserId(winner.getUserId());
        response.setUserName(winner.getUserName());
        return response;
    }

    private int sizeOf(List<?> list) {
        return list == null ? 0 : list.size();
    }
}
