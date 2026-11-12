package com.wunamor.lotterysystem.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.util.List;

/**
 * 本地图片上传配置，对应 application.yaml 里的 lottery.upload.*
 */
@Data
@Component
@ConfigurationProperties(prefix = "lottery.upload")
public class UploadProperties {

    /**
     * 上传根目录，默认项目根目录下的 uploads
     */
    private String dir = System.getProperty("user.dir") + "/uploads";

    /**
     * 对外访问前缀，图片 URL 形如 /uploads/prize/2026/11/11/xxx.png
     */
    private String urlPrefix = "/uploads";

    /**
     * 单文件大小上限
     */
    private DataSize maxSize = DataSize.ofMegabytes(5);

    /**
     * 允许的扩展名（小写、不带点）
     */
    private List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "webp");
}
