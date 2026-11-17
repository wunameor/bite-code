package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 创建活动请求：活动基本信息 + 圈选的奖品 + 圈选的人员。
 */
@Data
public class ActivityCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "活动名称不能为空")
    @Size(max = 255, message = "活动名称不能超过 255 个字符")
    private String activityName;

    @NotBlank(message = "活动描述不能为空")
    @Size(max = 255, message = "活动描述不能超过 255 个字符")
    private String description;

    @NotEmpty(message = "请至少选择一个奖品")
    @Valid
    private List<ActivityPrizeRequest> activityPrizeList;

    @NotEmpty(message = "请至少选择一个人员")
    @Valid
    private List<ActivityUserRequest> activityUserList;
}
