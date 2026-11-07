package com.wunamor.lotterysystem.common.errorcode;

public interface GlobalErrorCodeConstants {
    ErrorCode SUCCESS = new ErrorCode(200, "请求成功");
    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数错误");
    ErrorCode ERROR = new ErrorCode(500, "请求失败");

    // 未知错误
    ErrorCode UNKNOW = new ErrorCode(999, "发生未知错误");
}
