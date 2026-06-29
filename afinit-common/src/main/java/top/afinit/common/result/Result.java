package top.afinit.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 自动生成所有 Getter/Setter
@AllArgsConstructor // 自动生成全参构造
@NoArgsConstructor  // 自动生成无参构造
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;


    /**
     * 成功返回 - 仅状态码和提示
     * @param resultCode 状态信息
     * @return 返回Result包裹的泛型T
     * @param <T> 泛型
     */
    public static <T> Result<T> success(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(),resultCode.getMessage(),null);
    }


    /**
     * 成功返回 - 状态码,提示和数据
     * @param resultCode 状态信息
     * @return 返回Result包裹的泛型T
     * @param <T> 泛型
     */
    public static <T> Result<T> success(ResultCode resultCode, T data) {
        return new Result<>(resultCode.getCode(),resultCode.getMessage(),data);
    }

    /**
     * 失败返回 - 仅状态码和提示
     * @param resultCode 状态信息
     * @return 返回Result包裹的泛型T
     * @param <T> 泛型
     */
    public static <T> Result<T> error(ResultCode resultCode){
        return new Result<>(resultCode.getCode(),resultCode.getMessage(),null);
    }

    /**
     * 失败返回 - 状态码,提示和信息
     * @param resultCode 状态信息
     * @return 返回Result包裹的泛型T
     * @param <T> 泛型
     */
    public static <T> Result<T> error(ResultCode resultCode, T data){
        return new Result<>(resultCode.getCode(),resultCode.getMessage(),data);
    }

}
