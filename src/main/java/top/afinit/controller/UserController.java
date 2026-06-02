package top.afinit.controller;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.afinit.common.constant.AuthConstants;
import top.afinit.common.constant.FileConstants;
import top.afinit.common.constant.MailConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.CommonResultCode;
import top.afinit.domain.dto.*;
import top.afinit.common.result.Result;
import top.afinit.common.result.UserResultCode;
import top.afinit.domain.vo.LoginTokenVO;
import top.afinit.domain.vo.UserVO;
import top.afinit.service.MailService;
import top.afinit.service.UserService;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    /**
     * 用户登录模块,登录成功返回token
     * @param userLoginDTO 用户信息(用户名,密码)
     * @return 统一返回体包裹的token类信息
     */
    @PostMapping("/login")
    public Result<LoginTokenVO> login(@RequestBody @Validated UserLoginDTO userLoginDTO,
                                      @RequestHeader(value = AuthConstants.CLIENT_TYPE,
                                              required = false,
                                              defaultValue = AuthConstants.Param.WEB)
                                      String clientType){
        LoginTokenVO loginTokenVO = userService.login(userLoginDTO,clientType);
        return Result.success(UserResultCode.USER_LOGIN_OK,loginTokenVO);
    }

    @PostMapping("/refresh")
    public Result<LoginTokenVO> refresh(@RequestHeader(value = AuthConstants.REFRESH_HEADER,required = false)
                                            @NotBlank(message = "Token不能为空")
                                            String refreshToken,
                                        @RequestHeader(value = AuthConstants.CLIENT_TYPE,
                                                required = false,
                                                defaultValue = AuthConstants.Param.WEB)
                                            String clientType){
        LoginTokenVO loginTokenVO = userService.refreshToken(refreshToken,clientType);
        return Result.success(UserResultCode.AUTH_REFRESH_OK,loginTokenVO);

    }


    @GetMapping("/info")
    public Result<UserVO> getInfoByToken(){
        UserVO userVO = userService.getUserInfoByToken();
        return Result.success(UserResultCode.GET_USER_OK,userVO);
    }


    @PutMapping("/nickname")
    public Result<String> updateUserNickname(@RequestBody  @Validated UserUpdateNicknameDTO userUpdateNicknameDTO){
        String nickname = userService.updateUserNickname(userUpdateNicknameDTO);
        return Result.success(UserResultCode.UPDATE_USER_OK,nickname);
    }

    @PostMapping("/avatar")
    public Result<String> updateUserAvatar(@RequestParam(FileConstants.PARAM_FILE) MultipartFile file){
        // 增加文件合法性前置拦截，防止空文件透传到存储桶
        if (file.isEmpty()) {
            throw new BusinessException(CommonResultCode.PARAM_ERR);
        }
        String avatarUrl = userService.updateAvatar(file);
        return Result.success(UserResultCode.UPDATE_USER_OK,avatarUrl);
    }

    @PostMapping("/code")
    public Result<Void> getCodeByEmail(@RequestBody @Validated SendCodeDTO sendCodeDTO){
        mailService.sendVerificationCode(sendCodeDTO, MailConstants.SIGN_UP_PRE);
        return Result.success(UserResultCode.SEND_CODE_OK);
    }

    /**
     * 用户注册
     * @param userRegisterDTO 注册信息（用户名、密码、邮箱、验证码）
     * @return 统一返回体
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success(UserResultCode.USER_REGISTER_OK);
    }

    @PutMapping("/info")
    public Result<UserVO> updateUserInfo(@RequestBody @Validated UserUpdateInfoDTO userUpdateInfoDTO){
        UserVO userVO = userService.updateUserInfo(userUpdateInfoDTO);
        return Result.success(UserResultCode.UPDATE_USER_OK,userVO);
    }

    @DeleteMapping("{id}")
    public Result<Void> deleteUserByToken(@PathVariable
                                              @NotNull(message = "id不能为空")
                                              @Min(value = 1, message = "用户ID格式不合法")
                                              Long id){
        userService.deleteUserById(id);
        return Result.success(UserResultCode.DELETE_USER_OK);
    }


}
