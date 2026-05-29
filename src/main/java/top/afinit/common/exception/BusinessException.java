package top.afinit.common.exception;

import lombok.Getter;
import top.afinit.common.result.ResultCode;

@Getter
public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;


    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

}
