package com.q.reminder.reminder.contents;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.contents.Constants
 * @Description :
 * @date :  2022.11.17 18:17
 */
public interface Constants {
    public interface Jwt{
        /**
         * 密钥
         */
        String KEY = "123123";
        /**
         * 过期时间
         */
        long EXPIRATION = 7200000;
        /**
         * 请求头
         */
        String TOKEN_HEAD = "Authorization";
    }
}
