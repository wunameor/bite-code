package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.common.config.UploadProperties;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.service.dto.FileUploadDTO;
import com.wunamor.lotterysystem.service.enums.FileSceneEnum;
import com.wunamor.lotterysystem.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileServiceImplTest {

    @TempDir
    Path tempDir;

    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        UploadProperties properties = new UploadProperties();
        properties.setDir(tempDir.toString());
        properties.setUrlPrefix("/uploads");
        properties.setMaxSize(DataSize.ofMegabytes(5));
        properties.setAllowedExtensions(List.of("jpg", "jpeg", "png", "gif", "webp"));
        fileService = new FileServiceImpl(properties);
    }

    @Test
    void uploadPngReturnsUrlAndWritesFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", pngHeader());

        FileUploadDTO dto = fileService.uploadImage(FileSceneEnum.PRIZE, file);

        assertTrue(dto.getUrl().startsWith("/uploads/prize/"));
        assertTrue(dto.getUrl().endsWith(".png"));
        assertTrue(Files.exists(tempDir.resolve(dto.getUrl().substring("/uploads/".length()))));
    }

    @Test
    void emptyFileIsRejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", new byte[0]);

        ServiceException e = assertThrows(ServiceException.class,
                () -> fileService.uploadImage(FileSceneEnum.PRIZE, file));
        assertEquals(1021, e.getCode());
    }

    @Test
    void tooLargeFileIsRejected() {
        byte[] bytes = new byte[(int) DataSize.ofMegabytes(6).toBytes()];
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", bytes);

        ServiceException e = assertThrows(ServiceException.class,
                () -> fileService.uploadImage(FileSceneEnum.PRIZE, file));
        assertEquals(1022, e.getCode());
    }

    @Test
    void wrongExtensionIsRejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes());

        ServiceException e = assertThrows(ServiceException.class,
                () -> fileService.uploadImage(FileSceneEnum.PRIZE, file));
        assertEquals(1023, e.getCode());
    }

    @Test
    void fakePngIsRejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "not a png".getBytes());

        ServiceException e = assertThrows(ServiceException.class,
                () -> fileService.uploadImage(FileSceneEnum.PRIZE, file));
        assertEquals(1023, e.getCode());
    }

    @Test
    void nullSceneIsRejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", pngHeader());

        ServiceException e = assertThrows(ServiceException.class,
                () -> fileService.uploadImage(null, file));
        assertEquals(1025, e.getCode());
    }

    @Test
    void deleteByUrlRemovesUploadedFile() throws Exception {
        Path file = tempDir.resolve("prize/2026/11/11/test.png");
        Files.createDirectories(file.getParent());
        Files.write(file, pngHeader());

        fileService.deleteByUrl("/uploads/prize/2026/11/11/test.png");

        assertFalse(Files.exists(file));
    }

    @Test
    void deleteByUrlIgnoresPathOutsideUploadDir() throws Exception {
        Path outside = tempDir.getParent().resolve("outside-" + System.nanoTime() + ".png");
        Files.write(outside, pngHeader());

        fileService.deleteByUrl("/uploads/../" + outside.getFileName());

        assertTrue(Files.exists(outside));
        Files.deleteIfExists(outside);
    }

    @Test
    void deleteByUrlIgnoresNonUploadUrl() {
        fileService.deleteByUrl("/pic/logo.png");
    }

    @Test
    void deleteByUrlIgnoresMissingFile() {
        fileService.deleteByUrl("/uploads/prize/2026/11/11/missing.png");
    }

    private byte[] pngHeader() {
        return new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
    }
}
