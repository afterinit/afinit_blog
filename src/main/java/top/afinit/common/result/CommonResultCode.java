package top.afinit.common.result;

import lombok.Getter;

@Getter
public enum CommonResultCode implements ResultCode{
    // === 成功基准 ===
    SUCCESS(10200, "操作成功"),
    ERROR(10500, "系统运行异常"),

    // === 客户端通用错误 (104xx) ===
    PARAM_ERR(10401, "参数不合法"),
    PARAM_IS_BLANK(10402, "缺少参数"),
    DATA_NOT_EXIST(10404, "请求的数据资源不存在"),
    METHOD_NOT_ALLOWED(10405, "请求方法不允许"),

    // === 系统级通用错误 (105xx) ===
    SYSTEM_TIMEOUT_ERR(10502, "系统请求超时"),
    SYSTEM_UNKNOWN_ERR(10599, "系统未知错误"),
    BUSINESS_ERR(10600, "业务处理异常");

    private final Integer code;
    private final String message;

    CommonResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
