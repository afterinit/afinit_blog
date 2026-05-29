package top.afinit.common.exception;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import top.afinit.common.result.CommonResultCode;
import top.afinit.common.result.Result;


@Slf4j // 自动注入日志对象
@RestControllerAdvice
public class GlobalExceptionHandler{


    /**
     * 捕获自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("[业务异常][{} -> {}] 错误码: {}, 原因: {}",
                request.getMethod(),
                request.getRequestURI(),
                e.getResultCode().getCode(),
                e.getMessage());

        return Result.error(e.getResultCode());
    }


    /**
     * 捕获路径参数、单参数校验失败异常（如 @PathVariable 上的 @Range 失败）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolationException(ConstraintViolationException e,
                                                             HttpServletRequest request) {

        ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
        Class<?> annotationType = violation.getConstraintDescriptor().getAnnotation().annotationType();
        String errorMsg = violation.getMessage();

        log.warn("[参数校验异常][{} -> {}] 原因: {}", request.getMethod(), request.getRequestURI(), errorMsg);

        if (annotationType == NotNull.class) {
            return Result.error(CommonResultCode.PARAM_IS_BLANK, errorMsg);
        }

        return Result.error(CommonResultCode.PARAM_ERR, errorMsg);
    }

    /**
     *  捕获 404 路由找不到异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("请求路径不存在: {}", e.getResourcePath());
        return Result.error(CommonResultCode.DATA_NOT_EXIST);
    }

    /**
     * 捕获 DTO 对象参数不合法异常（如 @RequestBody 校验失败）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();

        String defaultMessage = null;
        if (fieldError != null) {
            defaultMessage = fieldError.getDefaultMessage();
        }

        log.warn("[对象参数校验异常][{} -> {}] 原因: {}", request.getMethod(), request.getRequestURI(), defaultMessage);

        return Result.error(CommonResultCode.PARAM_ERR, defaultMessage);
    }



    /**
     * 捕获系统未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        String logMessage = "\n=================== [系统运行异常] ===================\n" +
                "请求方式: " + request.getMethod() + "\n" +
                "请求路径: " + request.getRequestURI() + "\n" +
                "查询参数: " + request.getQueryString() + "\n" +
                "异常类型: " + e.getClass().getName() + "\n" +
                "异常原因: " + e.getMessage() + "\n" +
                "====================================================";

        log.error(logMessage, e);

        return Result.error(CommonResultCode.SYSTEM_UNKNOWN_ERR);
    }



}
