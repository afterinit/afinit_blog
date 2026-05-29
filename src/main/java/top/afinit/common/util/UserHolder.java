package top.afinit.common.util;


import cn.hutool.core.util.ObjectUtil;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.UserResultCode;
import top.afinit.domain.dto.UserContextDTO;

public class UserHolder {
    private static final ThreadLocal<UserContextDTO> TL = new ThreadLocal<>();

    public static void saveUser(UserContextDTO userContextDTO) {
        TL.set(userContextDTO);
    }

    public static UserContextDTO getUser() {
        return TL.get();
    }

    public static void removeUser() {
        TL.remove();
    }

    /**
     * 统一资源归属/越权判定
     * @param resourceOwnerId 资源所属的用户ID
     */
    public static void judgmentAuth(Long resourceOwnerId) {
        UserContextDTO userContextDTO = UserHolder.getUser();

        if (ObjectUtil.isEmpty(userContextDTO) || ObjectUtil.isEmpty(resourceOwnerId)) {
            throw new BusinessException(UserResultCode.AUTH_TOKEN_MISSING);
        }

        boolean isSelf = resourceOwnerId.equals(userContextDTO.getId());
        boolean isAdmin = isAdmin();
        //如果不是本人并且不是管理员则拦截
        if(!isSelf && !isAdmin){
            throw new BusinessException(UserResultCode.AUTH_PERMISSION_DENIED);
        }

    }

    public static boolean isAdmin(){
        UserContextDTO userContextDTO = UserHolder.getUser();

        if (ObjectUtil.isEmpty(userContextDTO)) {
            throw new BusinessException(UserResultCode.AUTH_TOKEN_MISSING);
        }
        return ObjectUtil.equal(1, userContextDTO.getRole());
    }
}
