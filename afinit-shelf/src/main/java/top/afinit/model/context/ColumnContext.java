package top.afinit.model.context;

import lombok.Data;

@Data
public class ColumnContext {
    /**
     * 原始字段名
     */
    private String columnName;

    /**
     * Java字段名
     */
    private String javaField;

    /**
     * SQL类型
     */
    private String sqlType;

    /**
     * Java类型
     */
    private String javaType;

    /**
     * 是否主键
     */
    private Boolean primary;

    /**
     * 是否非空
     */
    private Boolean notNull;

    /**
     * MyBatis-Plus主键策略
     */
    private String idType; // AUTO / ASSIGN_ID / INPUT

    /**
     * 字段注释
     */
    private String comment;

    /**
     * 是否需要@TableField
     */
    private Boolean tableFieldAnnotation;

    /**
     * DTO字段校验注解
     */
    private String validationAnnotation;
}
