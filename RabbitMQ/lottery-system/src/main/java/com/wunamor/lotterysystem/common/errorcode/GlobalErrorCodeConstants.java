package com.wunamor.lotterysystem.common.errorcode;

public interface GlobalErrorCodeConstants {
    ErrorCode SUCCESS = new ErrorCode(200, "请求成功");
    ErrorCode ERROR = new ErrorCode(500, "请求失败");

    // 未知错误
    ErrorCode UNKNOW = new ErrorCode(999, "发生未知错误");
}
