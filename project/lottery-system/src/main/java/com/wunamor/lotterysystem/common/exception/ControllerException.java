package com.wunamor.lotterysystem.common.exception;


import com.wunamor.lotterysystem.common.errorcode.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ControllerException extends RuntimeException {
    private int code;
    private String message;

    public ControllerException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ControllerException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
