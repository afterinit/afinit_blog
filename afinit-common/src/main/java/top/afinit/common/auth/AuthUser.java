package top.afinit.common.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class AuthUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 角色 0-普通用户,1-管理员
     */
    private Integer role;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 当前登录持有的最新可用 AccessToken
     */
    private String accessToken;
}
