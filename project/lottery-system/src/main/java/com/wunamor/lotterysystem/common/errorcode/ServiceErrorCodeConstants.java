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

    // ============ 文件上传错误码 ============
    ErrorCode FILE_IS_EMPTY = new ErrorCode(1021, "上传文件不能为空");
    ErrorCode FILE_TOO_LARGE = new ErrorCode(1022, "上传文件过大");
    ErrorCode FILE_TYPE_NOT_ALLOWED = new ErrorCode(1023, "只支持 jpg、jpeg、png、gif、webp 图片");
    ErrorCode FILE_UPLOAD_ERROR = new ErrorCode(1024, "图片上传失败，请稍后再试");
    ErrorCode FILE_SCENE_NOT_ALLOWED = new ErrorCode(1025, "不支持的文件业务场景");

    // ============ 活动模块错误码 ===========
    ErrorCode ACTIVITY_INFO_IS_EMPTY = new ErrorCode(1033, "活动信息为空");
    ErrorCode ACTIVITY_NAME_IS_EMPTY = new ErrorCode(1034, "活动名称不能为空");
    ErrorCode ACTIVITY_NAME_TOO_LONG = new ErrorCode(1035, "活动名称不能超过 255 个字符");
    ErrorCode ACTIVITY_DESCRIPTION_IS_EMPTY = new ErrorCode(1036, "活动描述不能为空");
    ErrorCode ACTIVITY_DESCRIPTION_TOO_LONG = new ErrorCode(1037, "活动描述不能超过 255 个字符");
    ErrorCode ACTIVITY_PRIZE_LIST_IS_EMPTY = new ErrorCode(1038, "请至少选择一个奖品");
    ErrorCode ACTIVITY_PRIZE_INVALID = new ErrorCode(1039, "奖品信息不合法或奖品不存在");
    ErrorCode ACTIVITY_USER_LIST_IS_EMPTY = new ErrorCode(1040, "请至少选择一个人员");
    ErrorCode ACTIVITY_USER_INVALID = new ErrorCode(1041, "人员信息不合法或人员不存在");
    ErrorCode ACTIVITY_USER_NOT_ENOUGH = new ErrorCode(1042, "选中人员数量不能少于奖品总数量");
    ErrorCode ACTIVITY_CREATE_ERROR = new ErrorCode(1043, "活动创建失败，请稍后再试");
    ErrorCode ACTIVITY_ID_IS_EMPTY = new ErrorCode(1044, "活动 id 不能为空");
    ErrorCode ACTIVITY_NOT_FOUND = new ErrorCode(1045, "活动不存在");


    // ============ 奖品模块错误码 ===========
    ErrorCode PRIZE_INFO_IS_EMPTY = new ErrorCode(1026, "奖品信息为空");
    ErrorCode PRIZE_NAME_IS_EMPTY = new ErrorCode(1027, "奖品名称不能为空");
    ErrorCode PRIZE_NAME_TOO_LONG = new ErrorCode(1028, "奖品名称不能超过 255 个字符");
    ErrorCode PRIZE_PRICE_INVALID = new ErrorCode(1029, "奖品价格必须大于 0 且不超过 99999999.99");
    ErrorCode PRIZE_DESCRIPTION_TOO_LONG = new ErrorCode(1030, "奖品描述不能超过 255 个字符");
    ErrorCode PRIZE_IMAGE_IS_EMPTY = new ErrorCode(1031, "请上传奖品图片");
    ErrorCode PRIZE_CREATE_ERROR = new ErrorCode(1032, "奖品创建失败，请稍后再试");

    // ============ 抽奖错误码 ===========
    ErrorCode DRAW_INFO_IS_EMPTY = new ErrorCode(1046, "抽奖信息为空");
    ErrorCode DRAW_ACTIVITY_NOT_RUNNING = new ErrorCode(1047, "活动已结束，不能抽奖");
    ErrorCode DRAW_PRIZE_INVALID = new ErrorCode(1048, "活动奖品不存在");
    ErrorCode DRAW_PRIZE_ALREADY_DRAWN = new ErrorCode(1049, "该奖品已抽取");
    ErrorCode DRAW_WINNER_COUNT_INVALID = new ErrorCode(1050, "中奖人数与奖品数量不一致");
    ErrorCode DRAW_WINNER_DUPLICATE = new ErrorCode(1051, "中奖人员重复");
    ErrorCode DRAW_WINNER_INVALID = new ErrorCode(1052, "中奖人员不存在或已中奖");
    ErrorCode DRAW_WINNER_CONTACT_INVALID = new ErrorCode(1053, "中奖人员邮箱或手机号缺失");
    ErrorCode DRAW_PRIZE_ERROR = new ErrorCode(1054, "抽奖失败，请稍后再试");
    ErrorCode DRAW_REQUEST_NOT_FOUND = new ErrorCode(1055, "抽奖请求不存在");
    ErrorCode DRAW_PRIZE_ORDER_INVALID = new ErrorCode(1056, "抽奖顺序错误，请先抽取当前奖品");
    ErrorCode DRAW_REQUEST_PROCESSING = new ErrorCode(1057, "该奖品正在处理中，请勿重复提交");
    ErrorCode DRAW_PRIZE_SLOT_NOT_ENOUGH = new ErrorCode(1063, "该奖品剩余中奖名额不足");

    // ============ 个人资料错误码 ===========
    ErrorCode PROFILE_INFO_IS_EMPTY = new ErrorCode(1058, "个人资料为空");
    ErrorCode PROFILE_OLD_PASSWORD_ERROR = new ErrorCode(1059, "原密码错误");
    ErrorCode PROFILE_PASSWORD_CONFIRM_ERROR = new ErrorCode(1060, "两次输入的新密码不一致");
    ErrorCode PROFILE_EMAIL_CODE_REQUIRED = new ErrorCode(1061, "修改邮箱需要填写验证码");
    ErrorCode PROFILE_USER_NAME_IS_EMPTY = new ErrorCode(1062, "用户名不能为空");

}
