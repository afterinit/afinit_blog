package top.afinit.service;

//验证服务
public interface CaptchaService {
    //人机验证
    void verifyTurnstile(String token);

    //验证码验证
    void validateCode(String email, String code);

    //检查用户名是否存在
    void checkUsernameOccupation(String username, Long excludeUserId);

}
