package com.wunamor.lotterysystem.controller;

import com.wunamor.lotterysystem.controller.response.FileUploadResponse;
import com.wunamor.lotterysystem.service.FileService;
import com.wunamor.lotterysystem.service.dto.FileUploadDTO;
import com.wunamor.lotterysystem.service.enums.FileSceneEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 图片上传：仅管理员可访问，文件保存到本地 uploads/{scene}/yyyy/MM/dd/ 下
     */
    @PostMapping("/upload/{scene}")
    public FileUploadResponse upload(@PathVariable("scene") String scene,
                                     @RequestParam(value = "file", required = false) MultipartFile file) {
        log.info("上传图片，scene: {}, fileName: {}, size: {}", scene,
                file == null ? null : file.getOriginalFilename(),
                file == null ? 0 : file.getSize());

        FileUploadDTO dto = fileService.uploadImage(FileSceneEnum.getByCode(scene), file);

        FileUploadResponse response = new FileUploadResponse();
        BeanUtils.copyProperties(dto, response);
        return response;
    }
}
