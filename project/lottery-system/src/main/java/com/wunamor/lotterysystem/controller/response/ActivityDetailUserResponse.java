package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

/**
 * 活动详情中的参与人响应。
 */
@Data
public class ActivityDetailUserResponse {

    private Long userId;

    private String userName;
}
