package top.afinit.service;


import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisService {

    //存储HashToken
    void saveTokenByHash(String tokenKey,
                         Map<String ,Object> map,
                         Long time,
                         TimeUnit timeUnit);

    //存储StringToken
    void saveTokenByStr(String tokenKey,
                        String tokenValue,
                        Long time,
                        TimeUnit timeUnit);

    //根据key获取Map数据
    Map<Object,Object> getTokenHash(String key);

    //根据key获取String数据
    String getTokenStr(String key);

    //删除accessRedis
    void rmAccessRedis();

    //更新accessToken的值
    <T> void addAccessValue(String hashKey, T value);



}
