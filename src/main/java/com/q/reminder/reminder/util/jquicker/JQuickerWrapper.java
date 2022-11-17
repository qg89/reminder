package com.q.reminder.reminder.util.jquicker;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;

public interface JQuickerWrapper extends JQuickerBase {

    //获取默认持续时间(单位自定义)--return int
    default long getDefaultExpirationTime(JWTDefaultExpirationTime.TimeUnit timeUnit) {
        long times = DEFAULT_EXPIRATION_TIME.getValue();
        if (timeUnit == JWTDefaultExpirationTime.TimeUnit.DAY) {
            times = times / 24 / 60 / 60;
        }else if(timeUnit == JWTDefaultExpirationTime.TimeUnit.HOUR){
            times = times / 24 / 60 ;
        }else if(timeUnit == JWTDefaultExpirationTime.TimeUnit.MINUTE){
            times = times  / 60 ;
        }else if (timeUnit == JWTDefaultExpirationTime.TimeUnit.MILL_SECOND){
            times = times * 1000;
        }else {

        }
        return times;
    }

    //该类方法仅仅返回的是默认的时间的持续时间，并不会返回剩余时间
    //获取默认持续时间(单位自定义)--return String
    default String getDefaultExpirationTimeToString(JWTDefaultExpirationTime.TimeUnit timeUnit) {
        int times = DEFAULT_EXPIRATION_TIME.getValue();
        if (timeUnit == JWTDefaultExpirationTime.TimeUnit.DAY) {
            times = times / 24 / 60 / 60;
        }else if(timeUnit == JWTDefaultExpirationTime.TimeUnit.HOUR){
            times = times / 24 / 60 ;
        }else if(timeUnit == JWTDefaultExpirationTime.TimeUnit.MINUTE){
            times = times  / 60 ;
        }else if (timeUnit == JWTDefaultExpirationTime.TimeUnit.MILL_SECOND){
            times = times * 1000;
        }else {

        }
        return times+timeUnit.getUnit();
    }

    //自定义加密密钥
    String initSecretKey(String selfDefineSecretKey);
    String initSecretKey(JWTDefaultSecretKey defaultSecretKey);
    //自定义加密算法
    SignatureAlgorithm initSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm);
    //自定义加密过期时间
    Date initExpirationTime(JWTDefaultExpirationTime expirationTime);

    //设置存储的数据(建议采用json形式储存值)
    Claims setStoreData(Map<String,String> data);
    //解析具体Header
     Header analyzeHeader(String token);
    //解析payload
    Claims analyzePayLoad(String token);
    //解析Signature
    String analyzeSignature(String token);
    //解析出具体的过期时间
    String analyzeExpirationTime(String token);
    //解析token是否过期
    boolean analyzeExpirationOrNot(String token);

}
