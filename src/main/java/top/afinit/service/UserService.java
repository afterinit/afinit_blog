package top.afinit.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.multipart.MultipartFile;
import top.afinit.domain.dto.*;
import top.afinit.domain.vo.LoginTokenVO;
import top.afinit.domain.vo.UserNicknameVO;
import top.afinit.domain.vo.UserVO;

import java.util.List;
import java.util.Set;

public interface UserService {

    //登录接口
    LoginTokenVO login(UserLoginDTO userLoginDTO, String clientType);

    //注册接口
    void register(UserRegisterDTO userRegisterDTO);

    //刷新accessToken
    LoginTokenVO refreshToken(String refreshToken,String clientType);

    //通过多个用户id批量获取user
    List<UserNicknameVO> listByIds(Set<Long> userIds);

    //通过单个用户id获取user
    UserContextDTO getById(Long userId);

    //通过Thread获取用户信息视图
    UserVO getUserInfoByToken();

    //更新用户信息
    String updateUserNickname(UserUpdateNicknameDTO userUpdateNicknameDTO);

    //更新用户头像
    String updateAvatar(MultipartFile file);

    //更新敏感用户信息
    UserVO updateUserInfo(UserUpdateInfoDTO userUpdateInfoDTO);

    //按页查询用户信息
    IPage<UserVO> getUserInfo(Long page,  Long size);

    //通过id删除用户
    void deleteUserById(Long id);

    //通过id停用/启用用户
    void blockUserById(Long id, Integer status);


}
