package top.afinit.model.request;

import lombok.Data;

@Data
public class ColumnRequest {
    /**
     * 数据库字段名
     */
    private String columnName;

    /**
     * SQL类型
     */
    private String sqlType;

    /**
     * 是否主键
     */
    private Boolean primary;

    /**
     * 是否非空
     */
    private Boolean notNull;

    /**
     * 字段说明
     */
    private String comment;
}
