package top.afinit.service.Impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import top.afinit.common.constant.MailConstants;
import top.afinit.common.constant.RedisConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.CommonResultCode;
import top.afinit.common.result.VerResultCode;
import top.afinit.common.util.RedisKeyUtil;
import top.afinit.config.properties.MailProperties;
import top.afinit.domain.dto.SendCodeDTO;
import top.afinit.service.CaptchaService;
import top.afinit.service.MailService;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MailProperties mailProperties;

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate stringRedisTemplate;
    private final CaptchaService captchaService;


    @Override
    public void sendVerificationCode(SendCodeDTO sendCodeDTO, String textPre) {

        //人机验证
        captchaService.verifyTurnstile(sendCodeDTO.getCfToken());

        String to = sendCodeDTO.getTo();

        if(StrUtil.isBlank(to)){
            throw new BusinessException(CommonResultCode.PARAM_ERR);
        }

        //60秒检验
        String lockKey = RedisKeyUtil.getVerificationCodeLockKey(to);
        String hashSent = stringRedisTemplate.opsForValue().get(lockKey);
        if(!StrUtil.isBlank(hashSent)){
            throw new BusinessException(VerResultCode.SEND_CODE_TOO_FREQUENT);
        }

        String code = RandomUtil.randomNumbers(6);
        //将验证码存入Redis
        String verVerificationCodeKey = RedisKeyUtil.getVerificationCodeKey(to);
        stringRedisTemplate.opsForValue().set(verVerificationCodeKey,
                code,
                RedisConstants.Code.VERIFICATION_CODE_TTL,
                RedisConstants.Code.VERIFICATION_CODE_UNIT);

        //将下次可发送验证码时间存入Redis(1分钟)
        stringRedisTemplate.opsForValue().set(lockKey,
                RedisConstants.Code.DEADLINE_CODE_VALUE,
                RedisConstants.Code.DEADLINE_CODE_TTL,
                RedisConstants.Code.DEADLINE_CODE_UNIT);


        //发送验证码
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(mailProperties.getUsername());
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(MailConstants.VERIFICATION_SUBJECT);
            simpleMailMessage.setText(textPre + code + MailConstants.SIGN_UP_AFT);

            javaMailSender.send(simpleMailMessage);
        }catch (Exception e){
            // 异常回滚：邮件发送失败，立刻抹除 Redis 中的验证码和频率锁
            stringRedisTemplate.delete(verVerificationCodeKey);
            stringRedisTemplate.delete(lockKey);

            // 向上抛出系统未知错误，触发全局异常处理器记录堆栈日志，并友好提示前端
            throw new BusinessException(CommonResultCode.SYSTEM_UNKNOWN_ERR);
        }



    }




}
