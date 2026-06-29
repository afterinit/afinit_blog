package top.afinit.common.result;

import lombok.Getter;

@Getter
public enum BarrageResultCode implements ResultCode{

    // === 弹幕业务状态码 (3005x - 3008x) ===
    BARRAGE_SEND_OK(30051, "发送弹幕成功"),
    BARRAGE_SEND_ERR(30050, "发送弹幕失败,请稍后再试"),

    BARRAGE_DELETE_OK(30061, "删除弹幕成功"),
    BARRAGE_DELETE_ERR(30060, "删除弹幕失败,请稍后再试"),

    BARRAGE_GET_OK(30071, "获取弹幕成功"),
    BARRAGE_GET_ERR(30070, "获取弹幕失败,请稍后再试"),

    BARRAGE_STATUS_OK(30081, "状态更新成功"),
    BARRAGE_STATUS_ERR(30080, "状态更新失败,请稍后再试"),

    BARRAGE_CONTENT_ILLEGAL(30090,"弹幕内容不合法");

    // 成员变量
    private final Integer code;
    private final String message;

    // 构造方法
    BarrageResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
