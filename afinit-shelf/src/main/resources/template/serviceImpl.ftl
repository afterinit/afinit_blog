package ${project.basePackage}.service.impl;

<#if project.generateCrud?? && project.generateCrud>
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${project.basePackage}.common.result.PageResponse;
import ${project.basePackage}.domain.dto.${table.className}DTO;
import ${project.basePackage}.query.${table.className}Query;
import ${project.basePackage}.domain.vo.${table.className}VO;
</#if>
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${project.basePackage}.mapper.${table.className}Mapper;
import ${project.basePackage}.domain.entity.${table.className};
import ${project.basePackage}.service.${table.className}Service;
import org.springframework.stereotype.Service;

<#if table.comment?? && table.comment?has_content>
/**
 * ${table.comment}
 */
</#if>
@Service
public class ${table.className}ServiceImpl
extends ServiceImpl<${table.className}Mapper, ${table.className}>
implements ${table.className}Service {

<#if project.generateCrud?? && project.generateCrud>
    @Override
    public PageResponse<${table.className}VO> page(${table.className}Query query) {
        Page<${table.className}> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<${table.className}> wrapper = new LambdaQueryWrapper<>();

<#list table.columns as col>
<#if !col.primary>
        if (query.get${col.javaField?cap_first}() != null<#if col.javaType == "String"> && !query.get${col.javaField?cap_first}().isBlank()</#if>) {
            wrapper.eq(${table.className}::get${col.javaField?cap_first}, query.get${col.javaField?cap_first}());
        }

</#if>
</#list>
        Page<${table.className}> result = this.page(page, wrapper);
        return PageResponse.of(
                result.getCurrent(),
                result.getSize(),
                result.getTotal(),
                result.getRecords().stream().map(this::toVO).toList()
        );
    }

    @Override
    public ${table.className}VO detail(${table.primaryColumn.javaType} ${table.primaryColumn.javaField}) {
        ${table.className} entity = this.getById(${table.primaryColumn.javaField});
        return entity == null ? null : toVO(entity);
    }

    @Override
    public Boolean create(${table.className}DTO dto) {
        return this.save(toEntity(dto));
    }

    @Override
    public Boolean update(${table.primaryColumn.javaType} ${table.primaryColumn.javaField}, ${table.className}DTO dto) {
        ${table.className} entity = toEntity(dto);
        entity.set${table.primaryColumn.javaField?cap_first}(${table.primaryColumn.javaField});
        return this.updateById(entity);
    }

    @Override
    public Boolean delete(${table.primaryColumn.javaType} ${table.primaryColumn.javaField}) {
        return this.removeById(${table.primaryColumn.javaField});
    }

    private ${table.className} toEntity(${table.className}DTO dto) {
        ${table.className} entity = new ${table.className}();
<#list table.columns as col>
<#if !col.primary>
        entity.set${col.javaField?cap_first}(dto.get${col.javaField?cap_first}());
</#if>
</#list>
        return entity;
    }

    private ${table.className}VO toVO(${table.className} entity) {
        ${table.className}VO vo = new ${table.className}VO();
<#list table.columns as col>
        vo.set${col.javaField?cap_first}(entity.get${col.javaField?cap_first}());
</#list>
        return vo;
    }
</#if>
}
