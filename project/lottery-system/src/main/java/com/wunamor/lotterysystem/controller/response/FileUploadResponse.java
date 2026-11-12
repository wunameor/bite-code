package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

/**
 * 图片上传响应
 */
@Data
public class FileUploadResponse {

    /**
     * 对外访问地址，例如 /uploads/prize/2026/11/11/xxx.png
     */
    private String url;

    private String fileName;
}
