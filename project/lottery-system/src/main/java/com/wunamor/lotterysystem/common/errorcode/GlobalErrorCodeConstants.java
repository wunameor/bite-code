package com.wunamor.lotterysystem.common.errorcode;

public interface GlobalErrorCodeConstants {
    ErrorCode SUCCESS = new ErrorCode(200, "请求成功");
    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数错误");
    ErrorCode UNAUTHORIZED = new ErrorCode(401, "未登录或登录已过期");
    ErrorCode FORBIDDEN = new ErrorCode(403, "无权限执行该操作");
    ErrorCode NOT_FOUND = new ErrorCode(404, "资源不存在");
    ErrorCode ERROR = new ErrorCode(500, "请求失败");

    // 未知错误
    ErrorCode UNKNOW = new ErrorCode(999, "发生未知错误");
}
