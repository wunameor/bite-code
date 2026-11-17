package com.wunamor.lotterysystem.service.command;

import lombok.Data;

/**
 * 创建活动时圈选的单个人员命令。
 */
@Data
public class ActivityUserCommand {

    private Long userId;
}
