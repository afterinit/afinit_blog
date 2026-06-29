package ${project.basePackage}.service;

import com.baomidou.mybatisplus.extension.service.IService;
<#if project.generateCrud?? && project.generateCrud>
import ${project.basePackage}.common.result.PageResponse;
import ${project.basePackage}.domain.dto.${table.className}DTO;
import ${project.basePackage}.query.${table.className}Query;
import ${project.basePackage}.domain.vo.${table.className}VO;
</#if>
import ${project.basePackage}.domain.entity.${table.className};

<#if table.comment?? && table.comment?has_content>
/**
 * ${table.comment}
 */
</#if>
public interface ${table.className}Service extends IService<${table.className}> {

<#if project.generateCrud?? && project.generateCrud>
    PageResponse<${table.className}VO> page(${table.className}Query query);

    ${table.className}VO detail(${table.primaryColumn.javaType} ${table.primaryColumn.javaField});

    Boolean create(${table.className}DTO dto);

    Boolean update(${table.primaryColumn.javaType} ${table.primaryColumn.javaField}, ${table.className}DTO dto);

    Boolean delete(${table.primaryColumn.javaType} ${table.primaryColumn.javaField});
</#if>
}
