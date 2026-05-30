package top.afinit.common.result;

import lombok.Getter;

@Getter
public enum UserResultCode implements ResultCode {
    // === 用户业务状态码 (200xx) ===
    USER_REGISTER_OK(20051, "注册成功"),
    USER_REGISTER_ERR(20050, "注册失败,请稍后再试"),
    USER_LOGIN_OK(20011, "登录成功"),
    USER_LOGIN_ERR(20010, "登录失败,请稍后再试"),

    GET_USER_OK(20031, "获取用户信息成功"),
    GET_USER_ERR(20030, "获取用户信息失败,请稍后再试"),
    UPDATE_USER_OK(20041, "更新用户信息成功"),
    UPDATE_USER_ERR(20040, "更新用户信息失败,请稍后再试"),

    USER_NOT_EXIST(20052, "用户不存在"),
    USER_ALREADY_EXIST(20053, "用户名已被占用"),
    USER_PASSWORD_ERR(20054, "密码错误"),
    USER_ACCOUNT_LOCKED(20055, "账号已被冻结"),

    DELETE_USER_OK(20061, "删除用户成功"),
    DELETE_USER_ERR(20060, "删除用户失败,请稍后再试"),

    // === 认证与权限状态码 (204xx) ===
    AUTH_REFRESH_OK(20411, "刷新凭证成功"),
    AUTH_TOKEN_EXPIRED(20410, "登录已过期,请重新登录"),
    AUTH_TOKEN_INVALID(20412, "凭证无效,请重新登录"),
    AUTH_TOKEN_MISSING(20413, "缺少访问凭证"),
    AUTH_PERMISSION_DENIED(20414, "权限不足,拒绝访问"),

    // === 验证码安全特有 (205xx) ===
    CAPTCHA_ERR(20501, "人机验证未通过或已失效，请刷新页面重试"),
    SEND_CODE_OK(20502, "发送验证码成功"),
    SEND_CODE_TOO_FREQUENT(20503, "验证码发送过于频繁，请在一分钟后再试"),
    VERIFICATION_CODE_ERR(20504, "验证码错误或已失效");

    // 提供对外的 Getter 方法
    // 成员变量
    private final Integer code;
    private final String message;

    // 构造方法
    UserResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }



}
