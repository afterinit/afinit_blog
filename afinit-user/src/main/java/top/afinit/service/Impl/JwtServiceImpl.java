package top.afinit.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import top.afinit.common.constant.RedisConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.AuthResultCode;
import top.afinit.config.properties.JwtProperties;
import top.afinit.service.JwtService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    private final SecretKey key;


    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String id, Long time) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + time);

        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public String refreshToken(String refreshToken, String oldValue) {

        //判断refreshToken是否为空
        if(StrUtil.isBlank(refreshToken)){
            throw new BusinessException(AuthResultCode.AUTH_TOKEN_MISSING);
        }

        //通过refreshToken获取值用户id
        String userId = parseToken(refreshToken);
        if(StrUtil.isBlank(userId)){
            throw new BusinessException(AuthResultCode.AUTH_TOKEN_INVALID);
        }



        String value = SecureUtil.md5(refreshToken);

        if(StrUtil.isBlank(oldValue)){
            //refreshToken已过期,需重新登录
            throw new BusinessException(AuthResultCode.AUTH_TOKEN_EXPIRED);
        }

        if(!oldValue.equals(value)){
            throw new BusinessException(AuthResultCode.AUTH_TOKEN_INVALID);
        }

        return createToken(userId, RedisConstants.User.ACCESS_TOKEN_MS_TTL);
    }


}
