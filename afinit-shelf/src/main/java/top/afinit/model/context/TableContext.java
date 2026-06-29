package top.afinit.model.context;

import lombok.Data;

import java.util.List;

@Data
public class TableContext {
    /**
     * 表名（数据库）
     */
    private String tableName;

    /**
     * Java类名
     */
    private String className;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 字段
     */
    private List<ColumnContext> columns;

    /**
     * 需要导入的Java类型
     */
    private List<String> imports;

    /**
     * 实体额外导入
     */
    private List<String> entityImports;

    /**
     * DTO额外导入
     */
    private List<String> dtoImports;

    /**
     * Query额外导入
     */
    private List<String> queryImports;

    /**
     * 主键字段
     */
    private ColumnContext primaryColumn;
}
