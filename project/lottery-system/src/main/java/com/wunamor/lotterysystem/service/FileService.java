package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.service.dto.FileUploadDTO;
import com.wunamor.lotterysystem.service.enums.FileSceneEnum;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传图片到本地磁盘，按业务场景分目录保存
     *
     * @param scene 业务场景，不能为空
     * @param file  图片文件
     * @return 对外访问地址
     */
    FileUploadDTO uploadImage(FileSceneEnum scene, MultipartFile file);

    /**
     * 删除上传目录下的文件，用于业务落库失败时清理孤儿文件。
     * 删除失败只记录日志，不抛异常。
     *
     * @param url 上传接口返回的相对 URL
     */
    void deleteByUrl(String url);
}
