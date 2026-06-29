package ${project.basePackage}.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
<#if table.entityImports??>
<#list table.entityImports as importType>
import ${importType};
</#list>
</#if>

<#if table.comment?? && table.comment?has_content>
/**
 * ${table.comment}
 */
</#if>
@Data
@TableName("${table.tableName}")
public class ${table.className} {

<#list table.columns as col>

<#if col.comment?? && col.comment?has_content>
    /**
     * ${col.comment}
     */
</#if>
<#if col.primary>
<#if col.tableFieldAnnotation?? && col.tableFieldAnnotation>
    @TableId(value = "${col.columnName}", type = IdType.${col.idType!"ASSIGN_ID"})
<#else>
    @TableId(type = IdType.${col.idType!"ASSIGN_ID"})
</#if>
<#elseif col.tableFieldAnnotation?? && col.tableFieldAnnotation>
    @TableField("${col.columnName}")
</#if>
    private ${col.javaType} ${col.javaField};

</#list>

}