package com.wunamor.lotterysystem.common.errorcode;

public interface ServiceErrorCodeConstants {
    // ============ 人员模块错误码 ===========
    ErrorCode REGISTER_INFO_IS_EMPTY = new ErrorCode(1000, "注册信息为空");
    ErrorCode REGISTER_PHONE_PATTERN_ERROR = new ErrorCode(1001, "注册的手机号格式错误");
    ErrorCode REGISTER_IDENTITY_PATTERN_ERROR = new ErrorCode(1002, "注册的身份信息错误");
    ErrorCode REGISTER_PASSWORD_IS_EMPTY = new ErrorCode(1003, "注册的管理员密码为空");
    ErrorCode REGISTER_PASSWORD_PATTERN_ERROR = new ErrorCode(1004, "注册的密码格式错误");
    ErrorCode REGISTER_MAIL_PATTERN_ERROR = new ErrorCode(1007, "注册的邮箱格式错误");
    ErrorCode REGISTER_MAIL_USED = new ErrorCode(1005, "邮箱已经被使用");
    ErrorCode REGISTER_PHONE_USED = new ErrorCode(1006, "号码已经被使用");

    // ============ 邮箱验证码错误码 ===========
    ErrorCode EMAIL_CODE_SEND_FREQUENT = new ErrorCode(1008, "验证码发送过于频繁，请 60 秒后再试");
    ErrorCode EMAIL_CODE_ERROR = new ErrorCode(1009, "验证码错误或已过期");
    ErrorCode EMAIL_SEND_ERROR = new ErrorCode(1010, "邮件发送失败，请稍后再试");

    // ============ 登录错误码 ===========
    ErrorCode LOGIN_INFO_IS_EMPTY = new ErrorCode(1011, "登录信息为空");
    ErrorCode LOGIN_TYPE_ERROR = new ErrorCode(1012, "不支持的登录方式");
    ErrorCode LOGIN_PASSWORD_IS_EMPTY = new ErrorCode(1013, "密码不能为空");
    ErrorCode LOGIN_EMAIL_CODE_IS_EMPTY = new ErrorCode(1014, "邮箱验证码不能为空");
    ErrorCode LOGIN_ACCOUNT_OR_PASSWORD_ERROR = new ErrorCode(1015, "账号或密码错误");
    ErrorCode LOGIN_ACCOUNT_OR_CODE_ERROR = new ErrorCode(1016, "账号或验证码错误");

    // ============ 账号标识错误码 ===========
    ErrorCode ACCOUNT_IS_EMPTY = new ErrorCode(1017, "账号不能为空");
    // 1018 已废弃：账号统一为 account 后，不再区分“邮箱不能为空/用户名不能为空”

    // ============ 注册校验错误码 ===========
    ErrorCode REGISTER_USER_NAME_USED = new ErrorCode(1019, "用户名已经被使用");
    ErrorCode REGISTER_USER_NAME_CONTAINS_AT = new ErrorCode(1020, "用户名不能包含 @");

    // ============ 活动模块错误码 ===========


    // ============ 奖品模块错误码 ===========


    // ============ 抽奖错误码 ===========

}
