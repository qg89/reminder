package com.q.reminder.reminder.util.jquicker;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Calendar;
import java.util.Date;

/**
 * 标识JQuicker必须要有的方法
 */
public interface JQuickerBase {

    //默认密钥
    String DEFAULT_SECRET_KEY = JWTDefaultSecretKey.DEFAULT_SECRET_KEY1.getValue();

    //默认加密算法
    SignatureAlgorithm DEFAULT_SIGN = SignatureAlgorithm.HS256;

    //默认持续时间长度
    JWTDefaultExpirationTime DEFAULT_EXPIRATION_TIME = JWTDefaultExpirationTime.DEFAULT_EXPIRATION_TIME;



    //初始化默认持续时间
    default Date initDefaultExpirationTime(){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND,JWTDefaultExpirationTime.HALF_MONTH.getValue());
        return instance.getTime();
    }
    //获取默认密钥
    default String getDefaultSecretKey(){
        return DEFAULT_SECRET_KEY;
    }
    //获取默认持续时间(单位s)
    default int getDefaultExpirationTime(){
        return DEFAULT_EXPIRATION_TIME.getValue();
    }
    //获取默认加密方法
    default SignatureAlgorithm getDefaultSign(){
        return DEFAULT_SIGN;
    }

    //默认构造器
    JQuickerBase defaultBuilder();

    //自定义构造器
    JQuickerBase defaultBuilder(boolean enable);
    //构造token
    String createToken();
    //解析token
    Jws analysisToken(String token);
    //判断token是否过期
    boolean inspectExpirationTime(String token);

}
