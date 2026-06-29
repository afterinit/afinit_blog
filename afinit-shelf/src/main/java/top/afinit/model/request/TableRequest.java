package top.afinit.model.request;

import lombok.Data;

import java.util.List;

@Data
public class TableRequest {

    /**
     * 数据库表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 字段列表
     */
    private List<ColumnRequest> columns;
}
