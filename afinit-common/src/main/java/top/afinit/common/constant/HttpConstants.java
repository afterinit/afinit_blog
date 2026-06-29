package top.afinit.common.constant;

public class HttpConstants {

    public static final String AUTH_HEADER = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String REFRESH_HEADER = "X-Refresh-Token";
    public static final String CLIENT_TYPE = "X-Client-Type";
    public static final String JSON_UTF8 = "application/json;charset=UTF-8";

    public static class Param {
        public static final String WEB = "web";
        public static final String APP = "app";
        public static final String BEARER = "Bearer ";

    }

    public static class MessageParam{
        public static final String KEY_MESSAGES = "messages";
        public static final String KEY_ROLE = "role";
        public static final String KEY_CONTENT = "content";
        public static final String ROLE_USER = "user";

    }

    public static class ResultParam{
        public static final String SIGNAL_UNSAFE = "unsafe";
    }

}
