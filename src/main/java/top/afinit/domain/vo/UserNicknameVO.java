package top.afinit.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserNicknameVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String nickname;
}
