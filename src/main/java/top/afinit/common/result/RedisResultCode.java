package top.afinit.common.result;

import lombok.Getter;

@Getter
public enum RedisResultCode implements ResultCode{

    // === 缓存中间件状态码 (400xx) ===
    REDIS_KEY_NOT_FOUND(40001, "缓存key不存在"),
    REDIS_CONNECTION_FLR(40002, "重连缓存失败");


    // 提供对外的 Getter 方法
    // 成员变量
    private final Integer code;
    private final String message;

    // 构造方法
    RedisResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
