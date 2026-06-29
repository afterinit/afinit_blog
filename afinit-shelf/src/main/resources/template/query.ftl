package ${project.basePackage}.query;

import lombok.Data;
<#if table.queryImports??>
<#list table.queryImports as importType>
import ${importType};
</#list>
</#if>

<#if table.comment?? && table.comment?has_content>
/**
 * ${table.comment} 查询对象
 */
</#if>
@Data
public class ${table.className}Query {

    private long pageNum = 1;

    private long pageSize = 10;

<#list table.columns as col>
<#if !col.primary>
<#if col.comment?? && col.comment?has_content>
    /**
     * ${col.comment}
     */
</#if>
    private ${col.javaType} ${col.javaField};

</#if>
</#list>
}
