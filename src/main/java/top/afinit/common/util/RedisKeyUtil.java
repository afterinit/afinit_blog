package top.afinit.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import top.afinit.common.constant.RedisConstants;

public class RedisKeyUtil {
    /**
     * Access Token Key：传入明文 token，内部自动做 MD5 加密并拼接
     */
    public static String getAccessKey(String accessToken) {
        String accessTokenMd5 = SecureUtil.md5(accessToken);
        return RedisConstants.User.ACCESS_TOKEN_KEY + accessTokenMd5;
    }

    /**
     * Refresh Token Key：接收解析后的 userId 和客户端类型进行拼接
     */
    public static String getRefreshKey(String userId, String clientType) {
        return RedisConstants.User.REFRESH_TOKEN_KEY + userId + StrUtil.COLON + clientType;
    }


    /**
     * 验证码 Key：只负责接收 userId 拼接
     */
    public static String getVerificationCodeKey(String to) {
        String key = SecureUtil.md5(to);
        return RedisConstants.Code.VERIFICATION_CODE + key;
    }

    /**
     * 验证码锁Key
     */
    public static String getVerificationCodeLockKey(String to) {
        String key = SecureUtil.md5(to);
        return RedisConstants.Code.DEADLINE_CODE + key ;
    }

    /**
     * accessToken与userId的映射关系的Key
     */
    public static String getUserIdToAccessTokenKey(String userId){
        return RedisConstants.User.MAPPING_TOKEN_KEY+userId;
    }
}
