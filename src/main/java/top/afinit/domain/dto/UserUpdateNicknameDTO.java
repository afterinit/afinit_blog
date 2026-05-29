package top.afinit.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateNicknameDTO {

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能超过20个字符")
    private String nickname;

}
