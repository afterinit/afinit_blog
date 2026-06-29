package top.afinit.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.afinit.common.auth.AuthHolder;
import top.afinit.common.auth.AuthUser;
import top.afinit.common.constant.HttpConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.AuthResultCode;
import top.afinit.common.result.Result;
import top.afinit.common.result.ResultCode;
import top.afinit.common.util.RedisKeyUtil;
import top.afinit.service.RedisService;

import java.io.IOException;
import java.util.Map;


@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final RedisService redisService;

    public AuthenticationInterceptor(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                @Nullable Exception ex){
        AuthHolder.removeUser();
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws IOException {
        // OPTIONS 预检请求直接放行
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (uri.startsWith("/blog") && !uri.startsWith("/blog/private") && HttpMethod.GET.matches(method)) {
            return true;
        }

        if(uri.startsWith("/barrage")&&HttpMethod.GET.matches(method)){
            return true;
        }


        String token = request.getHeader(HttpConstants.AUTH_HEADER);

        if (StrUtil.isBlank(token)|| !token.startsWith(HttpConstants.Param.BEARER)) {
            returnUnauthorized(response, AuthResultCode.AUTH_TOKEN_MISSING);
            return false;
        }

        String realToken = token.substring(7);

        try {

            String accessKey = RedisKeyUtil.getAccessKey(realToken);
            Map<Object, Object> map = redisService.getTokenHash(accessKey);

            if(ObjectUtil.isEmpty(map)){
                returnUnauthorized(response, AuthResultCode.AUTH_TOKEN_EXPIRED);
                return false;
            }

            AuthUser authUser = BeanUtil.toBean(map, AuthUser.class);
            authUser.setAccessToken(realToken);
            AuthHolder.setUser(authUser);
        }catch (ExpiredJwtException e) {
            returnUnauthorized(response, AuthResultCode.AUTH_TOKEN_EXPIRED);
            return false;
        } catch (JwtException e) {
            returnUnauthorized(response, AuthResultCode.AUTH_TOKEN_INVALID);
            return false;
        } catch (BusinessException e){
            returnUnauthorized(response,e.getResultCode());
            return false;
        } catch (Exception e) {
            returnUnauthorized(response, AuthResultCode.AUTH_TOKEN_EXPIRED);
            return false;
        }

        return true;

    }


    private void returnUnauthorized(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setContentType(HttpConstants.JSON_UTF8);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(JSONUtil.toJsonStr(Result.error(resultCode)));
    }
}
