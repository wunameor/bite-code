package com.wunamor.lotterysystem.service.command;

import lombok.Data;

import java.util.List;

/**
 * 创建活动命令：Service 层入参。
 */
@Data
public class ActivityCreateCommand {

    private String activityName;

    private String description;

    private List<ActivityPrizeCommand> activityPrizeList;

    private List<ActivityUserCommand> activityUserList;
}
