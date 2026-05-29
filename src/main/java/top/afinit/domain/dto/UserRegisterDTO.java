package top.afinit.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegisterDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;


    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 64, max = 64, message = "数据传输异常，密码安全校验未通过")
    private String password;


    /**
     * 邮箱
     */
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "邮箱格式不正确"
    )
    private String email;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须为6位数字")
    private String code;


}
