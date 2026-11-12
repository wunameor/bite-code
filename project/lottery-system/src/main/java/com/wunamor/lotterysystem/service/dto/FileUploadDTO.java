package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 文件上传结果
 */
@Data
public class FileUploadDTO {

    /**
     * 对外访问地址，例如 /uploads/prize/2026/11/11/xxx.png
     */
    private String url;

    /**
     * 落盘后的文件名（UUID + 扩展名）
     */
    private String fileName;
}
