package top.afinit.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class BarrageDTO {

    @NotNull(message = "博客ID不能为空")
    @Positive(message = "博客ID不合法")
    private Long blogId;

    /**
     * 弹幕内容
     */
    @NotBlank(message = "弹幕内容不能为空")
    @Size(max = 20, message = "弹幕长度不能超过20个字符")
    private String content;

    /**
     * 触发位置百分比(例如 50.25)
     */
    @NotNull(message = "弹幕触发位置不能为空")
    @DecimalMin(value = "0.00", message = "触发位置不能小于0%")
    @DecimalMax(value = "100.00", message = "触发位置不能超过100%")
    private BigDecimal scrollPercent;

}
