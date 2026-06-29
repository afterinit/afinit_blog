package ${project.basePackage}.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ${project.basePackage}.domain.entity.${table.className};

<#if table.comment?? && table.comment?has_content>
/**
 * ${table.comment}
 */
</#if>
public interface ${table.className}Mapper extends BaseMapper<${table.className}> {

}