package top.afinit.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.afinit.common.result.BlogResultCode;
import top.afinit.common.result.Result;
import top.afinit.common.result.UserResultCode;
import top.afinit.domain.vo.UserVO;
import top.afinit.service.BlogService;
import top.afinit.service.UserService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final BlogService blogService;
    private final UserService userService;

    /**
     * 通过id公开文章
     * @param id 文章id
     * @return 统一返回体
     */
    @PutMapping("/toPublic/{id}")
    public Result<Void> toPublicBlog(@PathVariable
                                     @NotNull(message = "id不能为空")
                                     @Min(value = 1,message = "文章ID格式不合法")
                                     Long id){
        blogService.publicBlog(id);
        return Result.success(BlogResultCode.UPDATE_OK);
    }

    /**
     * 分页查询待审核文章
     * @param page 页数
     * @param size 个数
     * @return 统一返回体包裹的用户视图对象
     */
    @GetMapping
    public Result<IPage<UserVO>> getUserInfo(@RequestParam(defaultValue = "1")  Long page,
                                             @RequestParam(defaultValue = "10") @Max(10) Long size){
        IPage<UserVO> userVOIPage = userService.getUserInfo(page,size);
        return Result.success(UserResultCode.GET_USER_OK,userVOIPage);
    }

    /**
     * 通过id停用用户
     * @param id 用户id
     * @return 统一返回体
     */
    @PutMapping("{id}")
    public Result<Void> blackUser(@PathVariable
                                      @NotNull(message = "id不能为空")
                                      @Min(value = 1,message = "用户ID格式不合法")
                                      Long id,
                                  @RequestParam
                                  @NotNull(message = "状态不能为空")
                                  Integer status){
        userService.blockUserById(id,status);
        return Result.success(UserResultCode.UPDATE_USER_OK);
    }


}
