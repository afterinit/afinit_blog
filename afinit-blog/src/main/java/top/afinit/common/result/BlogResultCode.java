package top.afinit.common.result;

import lombok.Getter;

@Getter
public enum BlogResultCode implements ResultCode {
    // === 博客增删改查业务状态码 (300xx) ===
    SAVE_OK(30011, "保存成功"),
    SAVE_ERR(30010, "保存失败,请稍后再试"),

    DELETE_OK(30021, "删除成功"),
    DELETE_ERR(30020, "删除失败,请稍后再试"),

    UPDATE_OK(30031, "更新成功"),
    UPDATE_ERR(30030, "更新失败,请稍后再试"),

    GET_OK(30041, "查询成功"),
    GET_ERR(30040, "查询失败,请稍后再试");



    // 提供对外的 Getter 方法
    // 成员变量
    private final Integer code;
    private final String message;

    // 构造方法
    BlogResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
