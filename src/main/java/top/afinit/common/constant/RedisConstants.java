package top.afinit.common.constant;

import java.util.concurrent.TimeUnit;

public final class RedisConstants {
    private RedisConstants(){}
    /**
     * ==========================================
     * 1. 用户模块 (User) 缓存配置
     * ==========================================
     */
    public static final class User {
        /**
         * AccessToken
         */
        // 缓存 Key 前缀
        public static final String ACCESS_TOKEN_KEY = "auth:token:access:";
        //时间
        public static final Long ACCESS_TOKEN_TTL = 30L;
        public static final Long ACCESS_TOKEN_MS_TTL = 30 * 60 * 1000L;
        // 时间单位
        public static final TimeUnit ACCESS_TOKEN_UNIT = TimeUnit.MINUTES;

        /**
         * RefreshToken
         */
        // 缓存 Key 前缀
        public static final String REFRESH_TOKEN_KEY = "auth:token:refresh:";
        //时间
        public static final Long REFRESH_TOKEN_TTL = 7L;
        public static final Long REFRESH_TOKEN_MS_TTL = 7 * 24 * 60 * 60 * 1000L;
        // 时间单位
        public static final TimeUnit REFRESH_TOKEN_UNIT = TimeUnit.DAYS;

        public static final class Param{
            public static final String FIELD_NICKNAME = "nickname";
            public static final String FIELD_AVATAR = "avatar";
        }


    }

    public static final class Code{
        public static final String VERIFICATION_CODE = "auth:code:";
        public static final Long VERIFICATION_CODE_TTL = 5L;
        public static final TimeUnit VERIFICATION_CODE_UNIT = TimeUnit.MINUTES;

        public static final String DEADLINE_CODE = "auth:dead:";
        public static final String DEADLINE_CODE_VALUE ="EXIST";
        public static final Long DEADLINE_CODE_TTL = 1L;
        public static final TimeUnit DEADLINE_CODE_UNIT = TimeUnit.MINUTES;

    }

    /**
     * ==========================================
     * 2. 博客文章模块 (Blog) 缓存配置
     * ==========================================
     */
    public static final class Blog {
        // 缓存 Key 前缀
        public static final String CACHE_KEY = "blog:article:cache:";
        // 专门给 Blog 设置的时间单位
        public static final TimeUnit CACHE_UNIT = TimeUnit.HOURS;
        // 缓存有效时间：2 小时
        public static final Long CACHE_TTL = 2L;
    }


}
