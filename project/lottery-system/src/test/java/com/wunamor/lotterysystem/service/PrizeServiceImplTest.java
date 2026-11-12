package com.wunamor.lotterysystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.pojo.PageResult;
import com.wunamor.lotterysystem.mapper.PrizeMapper;
import com.wunamor.lotterysystem.mapper.dataobject.PrizeDO;
import com.wunamor.lotterysystem.service.command.PrizeCreateCommand;
import com.wunamor.lotterysystem.service.command.PrizeListCommand;
import com.wunamor.lotterysystem.service.dto.FileUploadDTO;
import com.wunamor.lotterysystem.service.dto.PrizeCreateDTO;
import com.wunamor.lotterysystem.service.dto.PrizeDTO;
import com.wunamor.lotterysystem.service.enums.FileSceneEnum;
import com.wunamor.lotterysystem.service.impl.PrizeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrizeServiceImplTest {

    private PrizeMapper prizeMapper;
    private FileService fileService;
    private PrizeServiceImpl prizeService;

    @BeforeEach
    void setUp() {
        prizeMapper = mock(PrizeMapper.class);
        fileService = mock(FileService.class);
        prizeService = new PrizeServiceImpl(prizeMapper, fileService);
    }

    @Test
    void createPrizeUploadsImageAndInserts() {
        PrizeCreateCommand command = validCommand();
        MockMultipartFile file = pngFile();
        FileUploadDTO upload = uploadResult();
        when(fileService.uploadImage(FileSceneEnum.PRIZE, file)).thenReturn(upload);
        when(prizeMapper.insert(any(PrizeDO.class))).thenAnswer(invocation -> {
            PrizeDO prizeDO = invocation.getArgument(0);
            prizeDO.setId(18L);
            return 1;
        });

        PrizeCreateDTO dto = prizeService.createPrize(command, file);

        assertEquals(18L, dto.getPrizeId());
        verify(fileService).uploadImage(FileSceneEnum.PRIZE, file);
        verify(prizeMapper).insert(any(PrizeDO.class));
    }

    @Test
    void emptyNameIsRejected() {
        PrizeCreateCommand command = validCommand();
        command.setPrizeName("  ");

        ServiceException e = assertThrows(ServiceException.class,
                () -> prizeService.createPrize(command, pngFile()));
        assertEquals(1027, e.getCode());
    }

    @Test
    void invalidPriceIsRejected() {
        PrizeCreateCommand command = validCommand();
        command.setPrice(BigDecimal.ZERO);

        ServiceException e = assertThrows(ServiceException.class,
                () -> prizeService.createPrize(command, pngFile()));
        assertEquals(1029, e.getCode());
    }

    @Test
    void tooLongDescriptionIsRejected() {
        PrizeCreateCommand command = validCommand();
        command.setDescription("x".repeat(256));

        ServiceException e = assertThrows(ServiceException.class,
                () -> prizeService.createPrize(command, pngFile()));
        assertEquals(1030, e.getCode());
    }

    @Test
    void missingImageIsRejected() {
        ServiceException e = assertThrows(ServiceException.class,
                () -> prizeService.createPrize(validCommand(), null));
        assertEquals(1031, e.getCode());
    }

    @Test
    void insertFailureDeletesUploadedImage() {
        PrizeCreateCommand command = validCommand();
        MockMultipartFile file = pngFile();
        FileUploadDTO upload = uploadResult();
        when(fileService.uploadImage(FileSceneEnum.PRIZE, file)).thenReturn(upload);
        when(prizeMapper.insert(any(PrizeDO.class))).thenThrow(new RuntimeException("db error"));

        ServiceException e = assertThrows(ServiceException.class,
                () -> prizeService.createPrize(command, file));
        assertEquals(1032, e.getCode());
        verify(fileService).deleteByUrl(upload.getUrl());
    }

    @Test
    void listPrizesReturnsPage() {
        PrizeListCommand command = new PrizeListCommand();
        command.setCurrentPage(1);
        command.setPageSize(5);

        Page<PrizeDO> dbPage = new Page<>(1, 5);
        dbPage.setTotal(12);
        dbPage.setRecords(List.of(prizeDO(3L, "三等奖"), prizeDO(2L, "二等奖")));
        doReturn(dbPage).when(prizeMapper).selectPage(any(Page.class), any());

        PageResult<PrizeDTO> result = prizeService.listPrizes(command);

        assertEquals(12, result.getTotal());
        assertEquals(1, result.getCurrentPage());
        assertEquals(5, result.getPageSize());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getRecords().size());
        assertEquals(3L, result.getRecords().get(0).getPrizeId());
        assertEquals("三等奖", result.getRecords().get(0).getPrizeName());
    }

    @Test
    @SuppressWarnings("unchecked")
    void listPrizesNormalizesInvalidPage() {
        PrizeListCommand command = new PrizeListCommand();
        command.setCurrentPage(0);
        command.setPageSize(1000);

        Page<PrizeDO> dbPage = new Page<>(1, 100);
        dbPage.setTotal(0);
        dbPage.setRecords(List.of());
        doReturn(dbPage).when(prizeMapper).selectPage(any(Page.class), any());

        PageResult<PrizeDTO> result = prizeService.listPrizes(command);

        ArgumentCaptor<Page<PrizeDO>> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(prizeMapper).selectPage(pageCaptor.capture(), any());
        assertEquals(1L, pageCaptor.getValue().getCurrent());
        assertEquals(100L, pageCaptor.getValue().getSize());
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());
    }

    @Test
    void listPrizesEmpty() {
        PrizeListCommand command = new PrizeListCommand();

        Page<PrizeDO> dbPage = new Page<>(1, 10);
        dbPage.setTotal(0);
        dbPage.setRecords(List.of());
        doReturn(dbPage).when(prizeMapper).selectPage(any(Page.class), any());

        PageResult<PrizeDTO> result = prizeService.listPrizes(command);

        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotalPages());
    }

    private PrizeCreateCommand validCommand() {
        PrizeCreateCommand command = new PrizeCreateCommand();
        command.setPrizeName("iPhone 16");
        command.setDescription("一等奖");
        command.setPrice(new BigDecimal("6999.00"));
        return command;
    }

    private MockMultipartFile pngFile() {
        return new MockMultipartFile("prizePic", "test.png", "image/png",
                new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A});
    }

    private FileUploadDTO uploadResult() {
        FileUploadDTO upload = new FileUploadDTO();
        upload.setUrl("/uploads/prize/2026/11/11/test.png");
        upload.setFileName("test.png");
        return upload;
    }

    private PrizeDO prizeDO(Long id, String name) {
        PrizeDO prizeDO = new PrizeDO();
        prizeDO.setId(id);
        prizeDO.setName(name);
        prizeDO.setDescription(name + "描述");
        prizeDO.setPrice(new BigDecimal("100.00"));
        prizeDO.setImageUrl("/uploads/prize/test.png");
        return prizeDO;
    }
}
