package top.afinit.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ProjectRequest {
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 基础包名
     */
    private String basePackage;

    /**
     * 是否生成完整 CRUD（Controller/Service/DTO/VO/Query 等）
     */
    private Boolean generateCrud = false;

    /**
     * 多表结构
     */
    private List<TableRequest> tables;
}
