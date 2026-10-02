package top.afinit.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.afinit.common.constant.TurnstileConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.UserResultCode;
import top.afinit.common.result.VerResultCode;
import top.afinit.common.util.RedisKeyUtil;
import top.afinit.config.properties.TurnstileProperties;
import top.afinit.dao.UserDao;
import top.afinit.domain.entity.User;
import top.afinit.service.CaptchaService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
    private final TurnstileProperties turnstileProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserDao userDao;

    @Override
    public void verifyTurnstile(String token){

        Map<String ,Object> paramMap = new HashMap<>();
        paramMap.put(TurnstileConstants.PARAM_SECRET,turnstileProperties.getSecretKey());
        paramMap.put(TurnstileConstants.PARAM_RESPONSE,token);

        try {
            try(HttpResponse response = HttpUtil.createPost(turnstileProperties.getVerifyUrl())
                    .form(paramMap)
                    .timeout(3000)
                    .execute()) {
                String result = response.body();

                Boolean success = JSONUtil.parseObj(result).getBool(TurnstileConstants.FIELD_SUCCESS);
                if (!Boolean.TRUE.equals(success)) {
                    log.warn("[人机验证-失败]:token={}",token);
                    throw new BusinessException(VerResultCode.CAPTCHA_ERR);
                }
            }
        }catch (BusinessException e){
            throw e;
        }catch (Exception e){
            throw new BusinessException(VerResultCode.CAPTCHA_ERR);
        }
    }

    @Override
    public void validateCode(String email, String code) {
        String verificationCodeKey = RedisKeyUtil.getVerificationCodeKey(email);
        String realCode = stringRedisTemplate.opsForValue().get(verificationCodeKey);

        if (ObjectUtil.isEmpty(realCode) || !code.equals(realCode)) {
            log.warn("[验证码验证-失败]:email={},real_code={}, code={}",email, realCode, code);
            throw new BusinessException(VerResultCode.VERIFICATION_CODE_ERR);
        }
        log.info("[验证码验证-成功]:email={},real_code={}, code={}",email, realCode, code);
    }

    @Override
    public void checkUsernameOccupation(String username, Long excludeUserId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);

        if (ObjectUtil.isNotNull(excludeUserId)) {
            wrapper.ne(User::getId, excludeUserId);
        }

        if (userDao.selectCount(wrapper) > 0) {
            log.warn("[检查用户名是否重复-失败]:用户名重复。username={},user_id={}", username, excludeUserId);
            throw new BusinessException(UserResultCode.USER_ALREADY_EXIST);
        }
    }


}
