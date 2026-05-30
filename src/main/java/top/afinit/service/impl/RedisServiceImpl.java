package top.afinit.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.afinit.common.util.RedisKeyUtil;
import top.afinit.common.util.UserHolder;
import top.afinit.domain.dto.UserContextDTO;
import top.afinit.service.RedisService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveTokenByHash(String tokenKey, Map<String, Object> map, Long time, TimeUnit timeUnit) {
        // 将 Map 中的所有 Value 转换为 String 格式
        Map<String, String> stringMap = new HashMap<>();
        map.forEach((k, v) -> stringMap.put(k, v == null ? null : Convert.toStr(v)));
        stringRedisTemplate.opsForHash().putAll(tokenKey,stringMap);
        stringRedisTemplate.expire(tokenKey,time,timeUnit);
    }

    @Override
    public void saveTokenByStr(String tokenKey, String tokenValue, Long time, TimeUnit timeUnit) {

        stringRedisTemplate.opsForValue().set(tokenKey,tokenValue,time,timeUnit);

    }

    @Override
    public String getTokenStr(String key) {

        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Map<Object, Object> getTokenHash(String key) {

        return stringRedisTemplate.opsForHash().entries(key);
    }

    @Override
    public void rmRedis(String key) {

        stringRedisTemplate.delete(key);

    }

    @Override
    public <T> void addAccessValue(String hashKey, T value) {
        UserContextDTO userContextDTO = UserHolder.getUser();

        if (ObjectUtil.isEmpty(userContextDTO) || StrUtil.isBlank(userContextDTO.getAccessToken())) {
            return;
        }

        String accessKey = RedisKeyUtil.getAccessKey(userContextDTO.getAccessToken());

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(accessKey))) {
            // 使用 Convert.toStr 将泛型 T 的实际类型（String, Long 等）安全转化为 Redis 存储的 String
            stringRedisTemplate.opsForHash().put(accessKey, hashKey, Convert.toStr(value));
        }
    }


}
