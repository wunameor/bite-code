package com.wunamor.lotterysystem.controller;

import com.wunamor.lotterysystem.controller.request.WinningRecordShowRequest;
import com.wunamor.lotterysystem.controller.response.WinningRecordResponse;
import com.wunamor.lotterysystem.service.WinningRecordService;
import com.wunamor.lotterysystem.service.dto.WinningRecordDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 中奖记录查询接口。
 */
@RestController
@RequestMapping("/winning-records")
public class WinningRecordController {

    private final WinningRecordService winningRecordService;

    public WinningRecordController(WinningRecordService winningRecordService) {
        this.winningRecordService = winningRecordService;
    }

    /**
     * 查询活动中奖记录；登录用户即可查看，便于分享最终结果页。
     */
    @PostMapping("/show")
    public List<WinningRecordResponse> show(
            @Valid @RequestBody WinningRecordShowRequest request) {
        List<WinningRecordDTO> records = winningRecordService.show(
                request.getActivityId(), request.getPrizeId());
        return records.stream().map(this::convert).collect(Collectors.toList());
    }

    private WinningRecordResponse convert(WinningRecordDTO record) {
        WinningRecordResponse response = new WinningRecordResponse();
        response.setActivityId(record.getActivityId());
        response.setPrizeId(record.getPrizeId());
        response.setPrizeName(record.getPrizeName());
        response.setPrizeTier(record.getPrizeTier());
        response.setPrizeTierName(record.getPrizeTierName());
        response.setWinnerId(record.getWinnerId());
        response.setWinnerName(record.getWinnerName());
        response.setWinningTime(record.getWinningTime());
        return response;
    }
}
