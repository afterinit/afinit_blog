package ${project.basePackage}.domain.vo;

import lombok.Data;
<#if table.imports??>
<#list table.imports as importType>
import ${importType};
</#list>
</#if>

<#if table.comment?? && table.comment?has_content>
/**
 * ${table.comment} VO
 */
</#if>
@Data
public class ${table.className}VO {

<#list table.columns as col>
<#if col.comment?? && col.comment?has_content>
    /**
     * ${col.comment}
     */
</#if>
    private ${col.javaType} ${col.javaField};

</#list>
}
