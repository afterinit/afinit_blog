package top.afinit.common.result;

import lombok.Getter;

@Getter
public enum VerResultCode implements ResultCode{

    CAPTCHA_ERR(20501, "人机验证未通过或已失效，请刷新页面重试"),
    SEND_CODE_OK(20502, "发送验证码成功"),
    SEND_CODE_TOO_FREQUENT(20503, "验证码发送过于频繁，请在一分钟后再试"),
    VERIFICATION_CODE_ERR(20504, "验证码错误或已失效");

    // 提供对外的 Getter 方法
    // 成员变量
    private final Integer code;
    private final String message;

    // 构造方法
    VerResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
