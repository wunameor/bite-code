package com.wunamor.lotterysystem.service.impl;

import com.wunamor.lotterysystem.common.config.UploadProperties;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.service.FileService;
import com.wunamor.lotterysystem.service.dto.FileUploadDTO;
import com.wunamor.lotterysystem.service.enums.FileSceneEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final DateTimeFormatter DATE_PATH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final UploadProperties uploadProperties;

    public FileServiceImpl(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public FileUploadDTO uploadImage(FileSceneEnum scene, MultipartFile file) {
        validateUpload(scene, file);

        String extension = resolveExtension(file.getOriginalFilename());
        validateExtension(extension);
        validateContentType(file.getContentType());

        byte[] bytes = readBytes(file);
        validateMagic(bytes, extension);

        SavedFile savedFile = saveFile(scene, extension, bytes);

        FileUploadDTO dto = new FileUploadDTO();
        dto.setUrl(savedFile.url());
        dto.setFileName(savedFile.fileName());
        log.info("图片上传成功，url: {}, size: {}", dto.getUrl(), file.getSize());
        return dto;
    }

    private void validateUpload(FileSceneEnum scene, MultipartFile file) {
        if (scene == null) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_SCENE_NOT_ALLOWED);
        }
        if (file == null || file.isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_IS_EMPTY);
        }
        if (file.getSize() > uploadProperties.getMaxSize().toBytes()) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_TOO_LARGE);
        }
    }

    private void validateExtension(String extension) {
        if (!uploadProperties.getAllowedExtensions().contains(extension)) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_TYPE_NOT_ALLOWED);
        }
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_TYPE_NOT_ALLOWED);
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            log.error("读取上传文件失败", e);
            throw new ServiceException(ServiceErrorCodeConstants.FILE_UPLOAD_ERROR);
        }
    }

    private void validateMagic(byte[] bytes, String extension) {
        if (!matchesMagic(bytes, extension)) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_TYPE_NOT_ALLOWED);
        }
    }

    private SavedFile saveFile(FileSceneEnum scene, String extension, byte[] bytes) {
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        Path baseDir = Paths.get(uploadProperties.getDir()).toAbsolutePath().normalize();
        Path targetDir = baseDir.resolve(scene.getCode()).resolve(datePath).normalize();
        Path target = targetDir.resolve(fileName).normalize();
        // 防路径穿越：最终路径必须仍在 uploads 根目录下
        if (!target.startsWith(baseDir)) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_UPLOAD_ERROR);
        }

        try {
            Files.createDirectories(targetDir);
            Files.write(target, bytes);
        } catch (IOException e) {
            log.error("保存上传文件失败，path: {}", target, e);
            throw new ServiceException(ServiceErrorCodeConstants.FILE_UPLOAD_ERROR);
        }

        return new SavedFile(buildUrl(scene, datePath, fileName), fileName);
    }

    @Override
    public void deleteByUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return;
        }

        String prefix = normalizeUrlPrefix();
        if (!url.startsWith(prefix + "/")) {
            log.warn("跳过删除非上传目录文件：{}", url);
            return;
        }

        Path baseDir = Paths.get(uploadProperties.getDir()).toAbsolutePath().normalize();
        Path target = baseDir.resolve(url.substring(prefix.length() + 1)).normalize();
        if (!target.startsWith(baseDir)) {
            log.warn("跳过删除越界文件：{}", url);
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("删除上传文件失败，path: {}", target, e);
        }
    }

    private String buildUrl(FileSceneEnum scene, String datePath, String fileName) {
        return normalizeUrlPrefix() + "/" + scene.getCode() + "/" + datePath + "/" + fileName;
    }

    private String normalizeUrlPrefix() {
        String prefix = uploadProperties.getUrlPrefix();
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix;
    }

    private String resolveExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_TYPE_NOT_ALLOWED);
        }
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) {
            throw new ServiceException(ServiceErrorCodeConstants.FILE_TYPE_NOT_ALLOWED);
        }
        return originalFilename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * 校验文件头，避免把 .exe 改名成 .png 传上来
     */
    private boolean matchesMagic(byte[] bytes, String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> bytes.length >= 3
                    && (bytes[0] & 0xFF) == 0xFF
                    && (bytes[1] & 0xFF) == 0xD8
                    && (bytes[2] & 0xFF) == 0xFF;
            case "png" -> bytes.length >= 8
                    && (bytes[0] & 0xFF) == 0x89
                    && bytes[1] == 'P'
                    && bytes[2] == 'N'
                    && bytes[3] == 'G'
                    && (bytes[4] & 0xFF) == 0x0D
                    && (bytes[5] & 0xFF) == 0x0A
                    && (bytes[6] & 0xFF) == 0x1A
                    && (bytes[7] & 0xFF) == 0x0A;
            case "gif" -> bytes.length >= 6
                    && bytes[0] == 'G'
                    && bytes[1] == 'I'
                    && bytes[2] == 'F'
                    && bytes[3] == '8'
                    && (bytes[4] == '7' || bytes[4] == '9')
                    && bytes[5] == 'a';
            case "webp" -> bytes.length >= 12
                    && bytes[0] == 'R'
                    && bytes[1] == 'I'
                    && bytes[2] == 'F'
                    && bytes[3] == 'F'
                    && bytes[8] == 'W'
                    && bytes[9] == 'E'
                    && bytes[10] == 'B'
                    && bytes[11] == 'P';
            default -> false;
        };
    }

    private record  SavedFile(String url, String fileName) {
    }
}
