package com.wunamor.lotterysystem.common.pojo;

import com.wunamor.lotterysystem.common.errorcode.ErrorCode;
import com.wunamor.lotterysystem.common.errorcode.GlobalErrorCodeConstants;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CommonResult<T> {

    private int code;
    private String message;
    private T data;

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(GlobalErrorCodeConstants.SUCCESS.getCode(),
                GlobalErrorCodeConstants.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResult<T> error(int code, String message) {
        return new CommonResult<>(code, message, null);
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return new CommonResult<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
}
