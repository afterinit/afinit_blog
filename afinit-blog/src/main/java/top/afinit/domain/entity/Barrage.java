package top.afinit.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("tb_blog_barrage")
public class Barrage {
    /**
     * 主键ID（雪花ID）
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
     * 状态：0-隐藏，1-正常
     */
    private Integer status;

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

    /**
     * 逻辑删除：1-删除，0-未删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer deleted;

}
