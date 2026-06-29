package top.afinit.common.result;

import lombok.Getter;

@Getter
public enum AuthResultCode implements ResultCode{
    AUTH_REFRESH_OK(20411, "刷新凭证成功"),
    AUTH_TOKEN_EXPIRED(20410, "登录已过期,请重新登录"),
    AUTH_TOKEN_INVALID(20412, "凭证无效,请重新登录"),
    AUTH_TOKEN_MISSING(20413, "缺少访问凭证"),
    AUTH_PERMISSION_DENIED(20414, "权限不足,拒绝访问");

    // 提供对外的 Getter 方法
    // 成员变量
    private final Integer code;
    private final String message;

    // 构造方法
    AuthResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
