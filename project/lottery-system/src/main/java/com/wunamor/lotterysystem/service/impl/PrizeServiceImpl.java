package com.wunamor.lotterysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.pojo.PageResult;
import com.wunamor.lotterysystem.mapper.PrizeMapper;
import com.wunamor.lotterysystem.mapper.dataobject.PrizeDO;
import com.wunamor.lotterysystem.service.FileService;
import com.wunamor.lotterysystem.service.PrizeService;
import com.wunamor.lotterysystem.service.command.PrizeCreateCommand;
import com.wunamor.lotterysystem.service.command.PrizeListCommand;
import com.wunamor.lotterysystem.service.dto.FileUploadDTO;
import com.wunamor.lotterysystem.service.dto.PrizeCreateDTO;
import com.wunamor.lotterysystem.service.dto.PrizeDTO;
import com.wunamor.lotterysystem.service.enums.FileSceneEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrizeServiceImpl implements PrizeService {

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 255;
    private static final BigDecimal MAX_PRICE = new BigDecimal("99999999.99");
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final PrizeMapper prizeMapper;
    private final FileService fileService;

    public PrizeServiceImpl(PrizeMapper prizeMapper, FileService fileService) {
        this.prizeMapper = prizeMapper;
        this.fileService = fileService;
    }

    @Override
    public PrizeCreateDTO createPrize(PrizeCreateCommand command, MultipartFile prizePic) {
        checkPrizeInfo(command);
        checkPrizeImage(prizePic);

        FileUploadDTO upload = fileService.uploadImage(FileSceneEnum.PRIZE, prizePic);
        PrizeDO prizeDO = buildPrizeDO(command, upload.getUrl());
        savePrize(prizeDO);

        PrizeCreateDTO dto = new PrizeCreateDTO();
        dto.setPrizeId(prizeDO.getId());
        return dto;
    }

    @Override
    public PageResult<PrizeDTO> listPrizes(PrizeListCommand command) {
        Page<PrizeDO> page = buildPage(command);
        Page<PrizeDO> result = prizeMapper.selectPage(page,
                new LambdaQueryWrapper<PrizeDO>().orderByDesc(PrizeDO::getId));

        List<PrizeDTO> records = result.getRecords().stream()
                .map(this::convertToPrizeDTO)
                .collect(Collectors.toList());

        return new PageResult<>(records, result.getTotal(),
                result.getCurrent(), result.getSize());
    }

    /**
     * 构建分页参数；Service 被直接调用时也做一次兜底，避免非法页码。
     */
    private Page<PrizeDO> buildPage(PrizeListCommand command) {
        long currentPage = command == null ? 1 : Math.max(1, command.getCurrentPage());
        long pageSize = command == null ? DEFAULT_PAGE_SIZE
                : Math.min(Math.max(1, command.getPageSize()), MAX_PAGE_SIZE);
        return new Page<>(currentPage, pageSize);
    }

    /**
     * 把奖品数据库对象转成 Service 层列表项。
     */
    private PrizeDTO convertToPrizeDTO(PrizeDO prizeDO) {
        PrizeDTO dto = new PrizeDTO();
        dto.setPrizeId(prizeDO.getId());
        dto.setPrizeName(prizeDO.getName());
        dto.setDescription(prizeDO.getDescription());
        dto.setPrice(prizeDO.getPrice());
        dto.setImageUrl(prizeDO.getImageUrl());
        return dto;
    }

    /**
     * 校验奖品名称、价格、描述。
     */
    private void checkPrizeInfo(PrizeCreateCommand command) {
        if (command == null) {
            throw new ServiceException(ServiceErrorCodeConstants.PRIZE_INFO_IS_EMPTY);
        }
        if (!StringUtils.hasText(command.getPrizeName())) {
            throw new ServiceException(ServiceErrorCodeConstants.PRIZE_NAME_IS_EMPTY);
        }
        if (command.getPrizeName().length() > MAX_NAME_LENGTH) {
            throw new ServiceException(ServiceErrorCodeConstants.PRIZE_NAME_TOO_LONG);
        }
        if (command.getPrice() == null
                || command.getPrice().compareTo(BigDecimal.ZERO) <= 0
                || command.getPrice().compareTo(MAX_PRICE) > 0) {
            throw new ServiceException(ServiceErrorCodeConstants.PRIZE_PRICE_INVALID);
        }
        if (command.getDescription() != null
                && command.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ServiceException(ServiceErrorCodeConstants.PRIZE_DESCRIPTION_TOO_LONG);
        }
    }

    /**
     * 奖品图片必填，且不能是空文件。
     */
    private void checkPrizeImage(MultipartFile prizePic) {
        if (prizePic == null || prizePic.isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.PRIZE_IMAGE_IS_EMPTY);
        }
    }

    /**
     * 把命令对象转成数据库对象，并写入图片 URL。
     */
    private PrizeDO buildPrizeDO(PrizeCreateCommand command, String imageUrl) {
        PrizeDO prizeDO = new PrizeDO();
        prizeDO.setName(command.getPrizeName());
        prizeDO.setDescription(command.getDescription());
        prizeDO.setPrice(command.getPrice());
        prizeDO.setImageUrl(imageUrl);
        return prizeDO;
    }

    /**
     * 落库；失败时尽力删除已上传的图片，避免留下孤儿文件。
     */
    private void savePrize(PrizeDO prizeDO) {
        try {
            prizeMapper.insert(prizeDO);
        } catch (RuntimeException e) {
            log.error("奖品落库失败，准备清理已上传图片，imageUrl: {}", prizeDO.getImageUrl(), e);
            fileService.deleteByUrl(prizeDO.getImageUrl());
            throw new ServiceException(ServiceErrorCodeConstants.PRIZE_CREATE_ERROR);
        }
    }
}
