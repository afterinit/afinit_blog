package top.afinit.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BarrageVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属文章ID
     */
    private Long blogId;

    /**
     * 发送用户ID
     */
    private Long userId;

    /**
     * 弹幕内容
     */
    private String content;

    /**
     * 触发位置百分比(例如 50.25)
     */
    private BigDecimal scrollPercent;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
