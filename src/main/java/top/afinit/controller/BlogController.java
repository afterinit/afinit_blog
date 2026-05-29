package top.afinit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.afinit.common.result.Result;
import top.afinit.common.result.BlogResultCode;
import top.afinit.domain.dto.BlogDTO;
import top.afinit.domain.vo.BlogVO;
import top.afinit.service.BlogService;



/**
 * 博客管理接口
 * 提供博客的上传,查询,修改,删除等核心功能
 */
@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
@Validated
public class BlogController {

    private final BlogService blogService;


    /**
     * 分页获取博客信息
     * @param page 页数,默认1
     * @param size 每页文章数,默认10
     * @return 统一返回体包裹的分页数据
     */
    @GetMapping
    public Result<IPage<BlogVO>> getPublicByPage(@RequestParam(defaultValue = "1")  Long page,
                                                 @RequestParam(defaultValue = "10") @Max(10) Long size){

        IPage<BlogVO> blogVOIPage = blogService.getPublicByPage(page,size);

        return Result.success(BlogResultCode.GET_OK,blogVOIPage);
    }

    /**
     * 上传博客
     * @param blogDTO 博客内容
     * @return 统一返回体包裹的博客id
     */
    @PostMapping
    public Result<Long> save(@RequestBody @Validated BlogDTO blogDTO){
        Long id= blogService.save(blogDTO);
        return Result.success(BlogResultCode.SAVE_OK,id);

    }


    /**
     * 修改博客
     * @param blogDTO 新博客内容
     * @return 统一返回体包裹的code信息
     */
    @PutMapping
    public Result<Void> updateById(@RequestBody @Validated BlogDTO blogDTO){

        blogService.updateById(blogDTO);

        return Result.success(BlogResultCode.UPDATE_OK);
    }

    /**
     * 删除博客
     * @param id 博客唯一id
     * @return 统一返回体包裹的code信息
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable
                                   @NotNull(message = "id不能为空")
                                   @Min(value = 1, message = "文章ID格式不合法")
                                   Long id){
        blogService.delete(id);
        return Result.success(BlogResultCode.DELETE_OK);
    }

    /**
     * 查询博客具体信息
     * @param id 博客id
     * @return 统一返回体包裹的博客视图对象
     */
    @GetMapping("/{id}")
    public Result<BlogVO> getPublicById(@PathVariable
                                      @NotNull(message = "id不能为空")
                                      @Min(value = 1,message = "文章ID格式不合法")
                                      Long id){

        BlogVO blogVO = blogService.getPublicById(id);

        return Result.success(BlogResultCode.GET_OK,blogVO);
    }


    @GetMapping("/private/{id}")
    public Result<BlogVO> getPrivate(@PathVariable
                                         @NotNull(message = "id不能为空")
                                         @Min(value = 1,message = "文章ID格式不合法")
                                         Long id){
        BlogVO blogVO = blogService.getPrivateById(id);
        return Result.success(BlogResultCode.GET_OK,blogVO);
    }

    @GetMapping("/private")
    public Result<IPage<BlogVO>> getPrivateByPage(@RequestParam(defaultValue = "1")  Long page,
                                                 @RequestParam(defaultValue = "10") @Max(10) Long size){

        IPage<BlogVO> blogVOIPage = blogService.getPrivateByPage(page,size);

        return Result.success(BlogResultCode.GET_OK,blogVOIPage);
    }

    @PutMapping("/toPublic/{id}")
    public Result<Void> toPublicBlog(@PathVariable
                                         @NotNull(message = "id不能为空")
                                         @Min(value = 1,message = "文章ID格式不合法")
                                         Long id){
        blogService.publicBlog(id);
        return Result.success(BlogResultCode.UPDATE_OK);
    }


}
