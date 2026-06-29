package ${project.basePackage}.domain.dto;

import lombok.Data;
<#if table.dtoImports??>
<#list table.dtoImports as importType>
import ${importType};
</#list>
</#if>

<#if table.comment?? && table.comment?has_content>
/**
 * ${table.comment} DTO
 */
</#if>
@Data
public class ${table.className}DTO {

<#list table.columns as col>
<#if !col.primary>
<#if col.comment?? && col.comment?has_content>
    /**
     * ${col.comment}
     */
</#if>
<#if col.validationAnnotation?? && col.validationAnnotation?has_content>
    ${col.validationAnnotation}
</#if>
    private ${col.javaType} ${col.javaField};

</#if>
</#list>
}
