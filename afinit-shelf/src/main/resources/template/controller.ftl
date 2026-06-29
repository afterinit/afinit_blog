package ${project.basePackage}.controller;

<#if project.generateCrud?? && project.generateCrud>
import ${project.basePackage}.common.result.ApiResponse;
import ${project.basePackage}.common.result.PageResponse;
import ${project.basePackage}.domain.dto.${table.className}DTO;
import ${project.basePackage}.query.${table.className}Query;
import ${project.basePackage}.domain.vo.${table.className}VO;
import jakarta.validation.Valid;
</#if>
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ${project.basePackage}.service.${table.className}Service;
import org.springframework.web.bind.annotation.*;

<#if table.comment?? && table.comment?has_content>
/**
 * ${table.comment}
 */
</#if>
@RestController
@RequestMapping("/${table.className?lower_case}")
@RequiredArgsConstructor
@Validated
public class ${table.className}Controller {

    private final ${table.className}Service service;

<#if project.generateCrud?? && project.generateCrud>
    @GetMapping("/page")
    public ApiResponse<PageResponse<${table.className}VO>> page(${table.className}Query query) {
        return ApiResponse.success(service.page(query));
    }

    @GetMapping("/{${table.primaryColumn.javaField}}")
    public ApiResponse<${table.className}VO> detail(@PathVariable ${table.primaryColumn.javaType} ${table.primaryColumn.javaField}) {
        return ApiResponse.success(service.detail(${table.primaryColumn.javaField}));
    }

    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody ${table.className}DTO dto) {
        return ApiResponse.success(service.create(dto));
    }

    @PutMapping("/{${table.primaryColumn.javaField}}")
    public ApiResponse<Boolean> update(
            @PathVariable ${table.primaryColumn.javaType} ${table.primaryColumn.javaField},
            @Valid @RequestBody ${table.className}DTO dto
    ) {
        return ApiResponse.success(service.update(${table.primaryColumn.javaField}, dto));
    }

    @DeleteMapping("/{${table.primaryColumn.javaField}}")
    public ApiResponse<Boolean> delete(@PathVariable ${table.primaryColumn.javaType} ${table.primaryColumn.javaField}) {
        return ApiResponse.success(service.delete(${table.primaryColumn.javaField}));
    }
</#if>
}
