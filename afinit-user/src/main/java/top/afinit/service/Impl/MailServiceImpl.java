package top.afinit.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import top.afinit.common.constant.MailConstants;
import top.afinit.common.constant.RedisConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.CommonResultCode;
import top.afinit.common.result.UserResultCode;
import top.afinit.common.result.VerResultCode;
import top.afinit.common.util.RedisKeyUtil;
import top.afinit.config.properties.MailProperties;
import top.afinit.dao.UserDao;
import top.afinit.domain.dto.SendCodeDTO;
import top.afinit.domain.entity.User;
import top.afinit.service.CaptchaService;
import top.afinit.service.MailService;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final MailProperties mailProperties;

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate stringRedisTemplate;
    private final CaptchaService captchaService;
    private final UserDao userDao;


    @Override
    public void sendVerificationCode(SendCodeDTO sendCodeDTO, String textPre) {

        //人机验证
        captchaService.verifyTurnstile(sendCodeDTO.getCfToken());

        String to = sendCodeDTO.getTo();
        String username = sendCodeDTO.getUsername();

        if(StrUtil.isBlank(to)){
            if(StrUtil.isBlank(username)) {
                //若同时为空返回报错
                log.warn("[发送邮件-失败]:邮箱与用户名均为空");
                throw new BusinessException(CommonResultCode.PARAM_IS_BLANK);
            }else{
                //设置规则
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getUsername, username);

                //查询数据库数据
                User user = userDao.selectOne(wrapper);
                if (ObjectUtil.isEmpty(user)) {
                    log.warn("[发送邮件-失败]:用户名不存在。user_id={}", username);
                    throw new BusinessException(UserResultCode.USER_NOT_EXIST);
                }
                to = user.getEmail();
            }
        }else if(!StrUtil.isBlank(username)){
            //若同时存在邮箱和用户名返回报错
            log.warn("[发送邮件-失败]:用户名与邮箱同时存在。user_id={},email={}", username,to);
            throw new BusinessException(CommonResultCode.PARAM_ERR);
        }

        //60秒检验
        String lockKey = RedisKeyUtil.getVerificationCodeLockKey(to);
        String hashSent = stringRedisTemplate.opsForValue().get(lockKey);
        if(!StrUtil.isBlank(hashSent)){
            log.warn("[发送邮件-失败]:发送邮件过于频繁。email={}",to);
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
            log.info("[发送邮件-成功]:email={},code={}", to, code);
        }catch (Exception e){
            // 异常回滚：邮件发送失败，立刻抹除 Redis 中的验证码和频率锁
            stringRedisTemplate.delete(verVerificationCodeKey);
            stringRedisTemplate.delete(lockKey);

            log.warn("[发送邮件-失败]:email={}",to);
            // 向上抛出系统未知错误，触发全局异常处理器记录堆栈日志，并友好提示前端
            throw new BusinessException(CommonResultCode.SYSTEM_UNKNOWN_ERR);
        }



    }




}
