package com.wunamor.lotterysystem.controller;

import com.wunamor.lotterysystem.common.pojo.PageResult;
import com.wunamor.lotterysystem.controller.request.PrizeCreateRequest;
import com.wunamor.lotterysystem.controller.request.PrizeListRequest;
import com.wunamor.lotterysystem.controller.response.PrizeCreateResponse;
import com.wunamor.lotterysystem.controller.response.PrizeResponse;
import com.wunamor.lotterysystem.service.PrizeService;
import com.wunamor.lotterysystem.service.command.PrizeCreateCommand;
import com.wunamor.lotterysystem.service.command.PrizeListCommand;
import com.wunamor.lotterysystem.service.dto.PrizeCreateDTO;
import com.wunamor.lotterysystem.service.dto.PrizeDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/prize")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    /**
     * 创建奖品：multipart 表单字段直接绑定 PrizeCreateRequest，prizePic 是图片。
     * 仅管理员可访问，图片由 Service 内部调用上传服务保存。
     */
    @PostMapping("/create")
    public PrizeCreateResponse createPrize(
            @Valid @ModelAttribute PrizeCreateRequest request,
            @RequestPart(value = "prizePic", required = false) MultipartFile prizePic) {

        log.info("创建奖品，prizeName: {}, price: {}, fileName: {}",
                request.getPrizeName(), request.getPrice(),
                prizePic == null ? null : prizePic.getOriginalFilename());

        PrizeCreateCommand command = new PrizeCreateCommand();
        BeanUtils.copyProperties(request, command);

        PrizeCreateDTO dto = prizeService.createPrize(command, prizePic);

        PrizeCreateResponse response = new PrizeCreateResponse();
        response.setPrizeId(dto.getPrizeId());
        return response;
    }

    /**
     * 分页查询奖品列表，按 id 倒序；仅管理员可访问。
     */
    @GetMapping("/list")
    public PageResult<PrizeResponse> listPrizes(@Valid @ModelAttribute PrizeListRequest request) {
        log.info("查询奖品列表，currentPage: {}, pageSize: {}",
                request.getCurrentPage(), request.getPageSize());

        PrizeListCommand command = new PrizeListCommand();
        BeanUtils.copyProperties(request, command);

        PageResult<PrizeDTO> result = prizeService.listPrizes(command);
        List<PrizeResponse> records = result.getRecords().stream()
                .map(this::convertToPrizeResponse)
                .collect(Collectors.toList());

        return new PageResult<>(records, result.getTotal(), result.getCurrentPage(),
                result.getPageSize());
    }

    /**
     * 把 Service 层列表项转成 Controller 响应。
     */
    private PrizeResponse convertToPrizeResponse(PrizeDTO dto) {
        PrizeResponse response = new PrizeResponse();
        BeanUtils.copyProperties(dto, response);
        return response;
    }
}
