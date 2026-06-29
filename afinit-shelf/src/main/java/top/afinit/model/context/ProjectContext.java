package top.afinit.model.context;

import lombok.Data;

import java.util.List;

@Data
public class ProjectContext {

    private String projectName;

    private String databaseName;

    private String basePackage;

    /**
     * 是否生成完整 CRUD
     */
    private Boolean generateCrud;

    private List<TableContext> tables;
}
