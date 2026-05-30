package top.afinit.service;


public interface JwtService {

    //创建token,既可以是accessToken也可以是refreshToken
    String createToken(String id, Long time);

    //通过token获取值
    String parseToken(String token);

    //通过accessToken获取新的accessToken值
    String refreshToken(String refreshToken, String oldValue);



}
