package com.q.reminder.reminder.util;

import com.q.reminder.reminder.contents.Constants;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.JwtTokenUtils
 * @Description :
 * @date :  2022.11.17 18:15
 */
public class JwtTokenUtils implements Serializable {
    //生成token
    public static String createToken(String username) {
        return Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + Constants.Jwt.EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, Constants.Jwt.KEY).compressWith(CompressionCodecs.GZIP).compact();
    }

    //获取用户名
    public static String getUserName(String token) {
        return Jwts.parser().setSigningKey(Constants.Jwt.KEY).parseClaimsJws(token).getBody().getSubject();
    }
}
