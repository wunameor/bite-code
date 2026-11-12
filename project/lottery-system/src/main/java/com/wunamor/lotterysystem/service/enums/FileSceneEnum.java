package com.wunamor.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件业务场景：决定图片落在 uploads 下的哪个子目录。
 * <p>
 * 新增业务时加一个枚举值即可，不影响已有文件路径。
 */
@Getter
@AllArgsConstructor
public enum FileSceneEnum {

    PRIZE("prize", "奖品图片"),
    ;

    /**
     * 目录名，同时出现在 URL 里，例如 /uploads/prize/...
     */
    private final String code;

    private final String desc;

    public static FileSceneEnum getByCode(String code) {
        for (FileSceneEnum scene : values()) {
            if (scene.code.equals(code)) {
                return scene;
            }
        }
        return null;
    }
}
