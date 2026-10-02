package top.afinit.common.auth;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.AuthResultCode;

@Slf4j
public class AuthHolder {

    private static final ThreadLocal<AuthUser> TL = new ThreadLocal<>();

    public static void setUser(AuthUser user) {
        TL.set(user);
    }

    public static AuthUser getUser() {
        AuthUser authUser = TL.get();
        if(ObjectUtil.isEmpty(authUser)){
            log.error("[Token鉴权失败]:Token不存在");
            throw new BusinessException(AuthResultCode.AUTH_TOKEN_MISSING);
        }
        return authUser;
    }

    public static void removeUser() {
        TL.remove();
    }

    /**
     * 统一资源归属/越权判定
     * @param resourceOwnerId 资源所属的用户ID
     */
    public static void judgmentAuth(Long resourceOwnerId) {
        if (ObjectUtil.isEmpty(resourceOwnerId)) {
            log.error("[Token鉴权失败]:传入id为空");
            throw new BusinessException(AuthResultCode.AUTH_TOKEN_MISSING);
        }

        AuthUser authUser = getUser();

        boolean isSelf = resourceOwnerId.equals(authUser.getId());
        boolean isAdmin = isAdmin();
        //如果不是本人并且不是管理员则拦截
        if(!isSelf && !isAdmin){
            log.error("[Token鉴权失败]:越界访问，实际为user_id={},user_status={},欲访问user_id={}所属资源",authUser.getId(),authUser.getStatus(),resourceOwnerId);
            throw new BusinessException(AuthResultCode.AUTH_PERMISSION_DENIED);
        }
    }

    public static boolean isAdmin(){
        AuthUser authUser = getUser();
        return ObjectUtil.equal(1, authUser.getRole());
    }


}
