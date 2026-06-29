package top.afinit.interceptor;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.afinit.common.auth.AuthHolder;
import top.afinit.common.constant.HttpConstants;
import top.afinit.common.result.AuthResultCode;
import top.afinit.common.result.Result;
import top.afinit.common.result.ResultCode;

import java.io.IOException;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws IOException {

        // OPTIONS 预检请求直接放行
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        if(!AuthHolder.isAdmin()){
            returnForbidden(response, AuthResultCode.AUTH_PERMISSION_DENIED);
            return false;
        }

        return true;
    }


    private void returnForbidden(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setContentType(HttpConstants.JSON_UTF8);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(JSONUtil.toJsonStr(Result.error(resultCode)));
    }
}
