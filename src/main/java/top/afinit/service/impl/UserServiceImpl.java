package top.afinit.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.afinit.common.util.RedisKeyUtil;
import top.afinit.common.util.UserHolder;
import top.afinit.common.constant.RedisConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.CommonResultCode;
import top.afinit.config.properties.JwtProperties;
import top.afinit.common.result.UserResultCode;
import top.afinit.dao.UserDao;
import top.afinit.domain.dto.*;
import top.afinit.domain.entity.User;
import top.afinit.domain.vo.LoginTokenVO;
import top.afinit.domain.vo.UserNicknameVO;
import top.afinit.domain.vo.UserVO;
import top.afinit.service.*;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final JwtProperties jwtProperties;

    private final RedisService redisService;

    private final FileService fileService;

    private final JwtService jwtService;

    private final CaptchaService captchaService;


    @Override
    public LoginTokenVO login(UserLoginDTO userLoginDTO, String clientType) {

        //人机验证
        captchaService.verifyTurnstile(userLoginDTO.getCfToken());

        //根据用户名查询数据库
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userLoginDTO.getUsername());
        User user = userDao.selectOne(wrapper);

        //判断该用户数据库是否存在
        if(ObjectUtil.isEmpty(user)){
            throw new BusinessException(UserResultCode.USER_NOT_EXIST);
        }

        //查看是否被冻结
        if(user.getStatus()!=1){
            throw new BusinessException(UserResultCode.USER_ACCOUNT_LOCKED);
        }

        String inputPassword = userLoginDTO.getPassword();

        //数据库密码
        String dbPassword = user.getPassword();

        //判断密码是否正确
        if(!BCrypt.checkpw(inputPassword, dbPassword)){
            throw new BusinessException(UserResultCode.USER_PASSWORD_ERR);
        }


        String userId = String.valueOf(user.getId());
        //创建accessToken
        String accessToken = jwtService.createToken(userId, RedisConstants.User.ACCESS_TOKEN_MS_TTL);

        //将user转为适合存储的UserContextDTO
        UserContextDTO userContextDTO = BeanUtil.copyProperties(user, UserContextDTO.class);

        //将accessToken存入UserContextDTO
        userContextDTO.setAccessToken(accessToken);

        //将UserContextDTO转为Map
        Map<String, Object> userContextDROMap = BeanUtil.beanToMap(userContextDTO);

        //得到accessToken的key
        String accessKey = RedisKeyUtil.getAccessKey(accessToken);

        //将accessToken存入Redis
        redisService.saveTokenByHash(accessKey,
                userContextDROMap,
                RedisConstants.User.ACCESS_TOKEN_TTL,
                RedisConstants.User.ACCESS_TOKEN_UNIT);

        //创建refreshToken
        String refreshToken = jwtService.createToken(userId, RedisConstants.User.REFRESH_TOKEN_MS_TTL);

        //获取refreshToken的key
        String refreshKey = RedisKeyUtil.getRefreshKey(userId, clientType);

        //使用md5加密得到值
        String refreshValue = SecureUtil.md5(refreshToken);

        //将refreshToken存入Redis
        redisService.saveTokenByStr(refreshKey,
                refreshValue,
                RedisConstants.User.REFRESH_TOKEN_TTL,
                RedisConstants.User.REFRESH_TOKEN_UNIT);


        //将映射存入redis
        String userIdToAccessTokenKey = RedisKeyUtil.getUserIdToAccessTokenKey(userId);
        redisService.saveTokenByStr(userIdToAccessTokenKey,
                accessToken,
                RedisConstants.User.MAPPING_TOKEN_TTL,
                RedisConstants.User.MAPPING_TOKEN_UNIT);


        return LoginTokenVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(jwtProperties.getTokenType())
                .expiresIn(jwtProperties.getExpiration() / 1000)
                .build();


    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {

        //验证码和邮箱
        String code = userRegisterDTO.getCode();
        String to = userRegisterDTO.getEmail();

        //判断验证码是否正确
        captchaService.validateCode(to, code);

        //判断用户名是否存在
        String username = userRegisterDTO.getUsername();
        captchaService.checkUsernameOccupation(username, null);

        //对密码进行加盐哈希加密
        String encodedPassword = BCrypt.hashpw(userRegisterDTO.getPassword());
        User user = BeanUtil.copyProperties(userRegisterDTO, User.class);
        user.setPassword(encodedPassword);
        user.setNickname(username);


        //存入数据库
        userDao.insert(user);

        //删除redis存储的验证码
        String verificationCodeKey = RedisKeyUtil.getVerificationCodeKey(to);
        redisService.rmRedis(verificationCodeKey);

    }

    @Override
    public LoginTokenVO refreshToken(String refreshToken,String clientType) {


        String userId = jwtService.parseToken(refreshToken);

        //获取存储在redis的refreshToken的值
        String refreshKey = RedisKeyUtil.getRefreshKey(userId, clientType);
        String oldValue = redisService.getTokenStr(refreshKey);

        //通过refreshToken获取新accessToken
        String newAccessToken = jwtService.refreshToken(refreshToken,oldValue);

        //获取accessToken的key
        String accessKey = RedisKeyUtil.getAccessKey(newAccessToken);

        //从数据库获取最新数据
        User user = userDao.selectById(userId);
        UserContextDTO userContextDTO = BeanUtil.copyProperties(user, UserContextDTO.class);

        userContextDTO.setId(Convert.toLong(userId));
        userContextDTO.setAccessToken(newAccessToken);

        Map<String, Object> userMap = BeanUtil.beanToMap(userContextDTO);

        String userIdToAccessTokenKey = RedisKeyUtil.getUserIdToAccessTokenKey(userId);


        //将accessToken存储到redis
        redisService.saveTokenByHash(accessKey,
                userMap,
                RedisConstants.User.ACCESS_TOKEN_TTL,
                RedisConstants.User.ACCESS_TOKEN_UNIT);

        redisService.saveTokenByStr(userIdToAccessTokenKey,
                newAccessToken,
                RedisConstants.User.MAPPING_TOKEN_TTL,
                RedisConstants.User.MAPPING_TOKEN_UNIT);

        return LoginTokenVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType(jwtProperties.getTokenType())
                .expiresIn(jwtProperties.getExpiration() / 1000)
                .build();


    }

    @Override
    public List<UserNicknameVO> listByIds(Set<Long> userIds) {
        if(CollUtil.isEmpty(userIds)){
            return new ArrayList<>();
        }

        List<Long> userIdList = new ArrayList<>(userIds);
        List<User> finalUserList = new ArrayList<>();

        // 将大 ID 集合按每批 1000 个进行切分，防止 SQL 的 IN 字段过多导致索引失效或撑爆内存
        List<List<Long>> partitionedIds = CollUtil.split(userIdList, 1000);


        // 循环分批查询，并将结果合并
        for (List<Long> batchIds : partitionedIds) {

            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            // 限制只查询 id 和 nickname 两个核心字段
            wrapper.select(User::getId, User::getNickname);
            // 使用 in 关键字批量匹配这一批的 1000 个 ID
            wrapper.in(User::getId, batchIds);


            List<User> batchUsers = userDao.selectList(wrapper);
            if (CollUtil.isNotEmpty(batchUsers)) {
                finalUserList.addAll(batchUsers);
            }
        }
        return BeanUtil.copyToList(finalUserList, UserNicknameVO.class);

    }

    @Override
    public UserContextDTO getById(Long userId) {
        UserHolder.judgmentAuth(userId);

        User user = userDao.selectById(userId);
        if(ObjectUtil.isEmpty(user)){
            throw new BusinessException(UserResultCode.USER_NOT_EXIST);
        }
        return BeanUtil.copyProperties(user,UserContextDTO.class);
    }


    @Override
    public UserVO getUserInfoByToken() {
        UserContextDTO userContextDTO = UserHolder.getUser();
        if(ObjectUtil.isEmpty(userContextDTO)){
            throw new BusinessException(UserResultCode.USER_NOT_EXIST);
        }

        UserHolder.judgmentAuth(userContextDTO.getId());

        return BeanUtil.copyProperties(userContextDTO, UserVO.class);


    }

    @Override
    public String updateUserNickname(UserUpdateNicknameDTO userUpdateNicknameDTO) {

        if(ObjectUtil.isEmpty(userUpdateNicknameDTO)){
            throw new BusinessException(CommonResultCode.PARAM_IS_BLANK);
        }

        //从 UserHolder 中获取当前真实登录用户的 ID并判断是否存在
        UserContextDTO currentUser = UserHolder.getUser();
        if(ObjectUtil.isEmpty(currentUser)|| ObjectUtil.isEmpty(currentUser.getId())){
            throw new BusinessException(UserResultCode.USER_NOT_EXIST);
        }

        Long realUserId = currentUser.getId();

        User user = BeanUtil.copyProperties(userUpdateNicknameDTO, User.class);
        user.setId(realUserId);

        userDao.updateById(user);


        String nickname = userUpdateNicknameDTO.getNickname();
        redisService.addAccessValue(RedisConstants.User.Param.FIELD_NICKNAME,
                nickname);

        return nickname;

    }

    @Override
    public String updateAvatar(MultipartFile file) {
        UserContextDTO currentUser = UserHolder.getUser();
        if(ObjectUtil.isEmpty(currentUser)|| ObjectUtil.isEmpty(currentUser.getId())){
            throw new BusinessException(UserResultCode.USER_NOT_EXIST);
        }

        Long realUserId = currentUser.getId();

        User user = userDao.selectById(realUserId);
        if(ObjectUtil.isEmpty(user)){
            throw new BusinessException(UserResultCode.USER_NOT_EXIST);
        }

        //删除原先存储的头像,若为空直接返回
        String oldAvatarUrl = user.getAvatar();
        fileService.deleteFile(oldAvatarUrl);

        //上传当前头像并返回头像的url
        String newAvatarUrl = fileService.uploadAvatar(file);

        //将新的头像url传入数据库
        User newUser = new User();
        newUser.setId(realUserId);
        newUser.setAvatar(newAvatarUrl);
        userDao.updateById(newUser);

        redisService.addAccessValue(RedisConstants.User.Param.FIELD_AVATAR, newAvatarUrl);

        return newAvatarUrl;


    }

    @Override
    public UserVO updateUserInfo(UserUpdateInfoDTO userUpdateInfoDTO) {

        UserContextDTO userContextDTO = UserHolder.getUser();
        Long userId = userContextDTO.getId();

        //验证码和邮箱
        String code = userUpdateInfoDTO.getCode();
        String to = userUpdateInfoDTO.getEmail();


        captchaService.validateCode(to,code);
        String username = userUpdateInfoDTO.getUsername();
        captchaService.checkUsernameOccupation(username,userId);

        User user = userDao.selectById(userId);
        if (ObjectUtil.isEmpty(user)) {
            throw new BusinessException(UserResultCode.USER_NOT_EXIST);
        }

        String encodedPassword = BCrypt.hashpw(userUpdateInfoDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setUsername(username);

        userDao.updateById(user);

        String verificationCodeKey = RedisKeyUtil.getVerificationCodeKey(to);
        redisService.rmRedis(verificationCodeKey);
        return BeanUtil.copyProperties(user, UserVO.class);

    }

    @Override
    public IPage<UserVO> getUserInfo(Long page, Long size) {

        boolean isAdmin = UserHolder.isAdmin();
        if(!isAdmin){
            throw new BusinessException(UserResultCode.AUTH_PERMISSION_DENIED);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userIPage = new Page<>(page, size);
        Page<User> userPage = userDao.selectPage(userIPage, wrapper);

        return userPage.convert(user -> {
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(user,userVO);
            return userVO;
        });
    }

    @Override
    public void deleteUserById(Long id) {
        UserContextDTO userContextDTO = UserHolder.getUser();

        if(ObjectUtil.isEmpty(userContextDTO)){
            throw new BusinessException(UserResultCode.AUTH_TOKEN_MISSING);
        }

        UserHolder.judgmentAuth(id);

        //redis删除

        rmRedis(id);

        //数据库删除
        userDao.deleteById(id);

    }

    @Override
    public void blockUserById(Long id, Integer status) {
        boolean isAdmin = UserHolder.isAdmin();
        if(!isAdmin){
            throw new BusinessException(UserResultCode.AUTH_PERMISSION_DENIED);
        }

        User user = new User();
        user.setId(id);
        user.setStatus(status);

        userDao.updateById(user);
        rmRedis(id);
    }


    private void rmRedis(Long id){
        String idStr = String.valueOf(id);
        String userIdToAccessTokenKey = RedisKeyUtil.getUserIdToAccessTokenKey(idStr);
        String accessToken = redisService.getTokenStr(userIdToAccessTokenKey);
        String accessKey = RedisKeyUtil.getAccessKey(accessToken);
        String refreshKeyWeb = RedisKeyUtil.getRefreshKey(idStr, "web");
        String refreshKeyApp = RedisKeyUtil.getRefreshKey(idStr, "app");

        redisService.rmRedis(accessKey);
        redisService.rmRedis(refreshKeyWeb);
        redisService.rmRedis(refreshKeyApp);
        redisService.rmRedis(userIdToAccessTokenKey);
    }


}
