package top.afinit.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SendCodeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 人机验证
     */
    @NotBlank(message = "未进行人机验证")
    private String cfToken;


    // 使用严格的正则表达式，只允许标准邮箱格式，任何换行符、控制符、敏感特殊符号直接被拦截
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "邮箱格式不正确"
    )
    private String to;


}
