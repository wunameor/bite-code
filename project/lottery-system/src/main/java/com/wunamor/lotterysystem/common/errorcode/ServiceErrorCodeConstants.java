package com.wunamor.lotterysystem.common.errorcode;

public interface ServiceErrorCodeConstants {
    // ============ 人员模块错误码 ===========
    ErrorCode REGISTER_INFO_IS_EMPTY = new ErrorCode(1000, "注册信息为空");
    ErrorCode REGISTER_PHONE_PATTERN_ERROR = new ErrorCode(1001, "注册的手机号格式错误");
    ErrorCode REGISTER_IDENTITY_PATTERN_ERROR = new ErrorCode(1002, "注册的身份信息错误");
    ErrorCode REGISTER_PASSWORD_IS_EMPTY = new ErrorCode(1003, "注册的手机号为空为空");
    ErrorCode REGISTER_PASSWORD_PATTERN_ERROR = new ErrorCode(1004, "注册的密码格式错误");
    ErrorCode REGISTER_MAIL_PATTERN_ERROR = new ErrorCode(1004, "注册的邮箱格式错误");
    ErrorCode REGISTER_MAIL_USED = new ErrorCode(1005, "邮箱已经被使用");
    ErrorCode REGISTER_PHONE_USED = new ErrorCode(1006, "号码已经被使用");

    // ============ 活动模块错误码 ===========


    // ============ 奖品模块错误码 ===========


    // ============ 抽奖错误码 ===========

}
