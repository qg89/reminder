package com.q.reminder.reminder.util.jquicker;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.val;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 诸天炁荡荡，我道日兴隆
 * JQuicker v1.2.0 旨在开箱即用，几乎无需任何学习成本
 */

/** 使用示例
 * 使用示例（默认方式）
 *         final val jQuicker = new JQuicker();
 *         jQuicker.defaultBuilder();
 *         final val hashMap = new HashMap<String, String>();
 *         hashMap.put("user","zhamsag");
 *         jQuicker.setStoreData(hashMap);
 *         final val token = jQuicker.createToken();
 *         System.out.println(token);
 *
 * 使用示例（自定义方式）
 *         final val jQuicker = new JQuicker();
 *         jQuicker.defaultBuilder(false);
 *         jQuicker.initSignatureAlgorithm(SignatureAlgorithm.HS384);
 *         jQuicker.initSecretKey(JWTDefaultSecretKey.HS384_SECRET_KEY);
 *         jQuicker.initExpirationTime(JWTDefaultExpirationTime.HALF_MONTH);
 *         final val hashMap = new HashMap<String, String>();
 *         hashMap.put("user","zhamsag");
 *         jQuicker.setStoreData(hashMap);
 *         final val token = jQuicker.createToken();
 *         System.out.println(token);
 *         final val claims = jQuicker.analyzePayLoad(token);
 *         claims.forEach((s, o) -> System.out.println(s+":-:"+o));
 *         final val jwsHeader = jQuicker.analyzeHeader(token);
 *         System.out.println(jwsHeader);
 *         final val restTime = jQuicker.getRestTime(token, JWTDefaultExpirationTime.TimeUnit.HOUR);
 *         System.out.println(restTime);
 */
public class JQuicker implements JQuickerWrapper {
    //设置密钥
    //key的大小必须大于或等于256bit,需要32位英文字符，
    // 一个英文字符为：8bit（1位）,一个中文字符为16bit（2位）
    String SECRET_KEY = null;
    //设置加密算法
    SignatureAlgorithm SIGN = null;
    //设置token持续时间
    JWTDefaultExpirationTime EXPIRATION_TIME = null;
    //是否使用默认构造
    boolean defaultEnable = true;
    //用户数据
    Claims USER_DATA = null;
    //token可持续时间

    public JQuicker() {

    }

    /**
     * 默认构造器（直接构造）
     *
     * @return
     */
    @Override
    public JQuickerBase defaultBuilder() {
        this.SECRET_KEY = DEFAULT_SECRET_KEY;
        this.SIGN = DEFAULT_SIGN;
        this.EXPIRATION_TIME = DEFAULT_EXPIRATION_TIME;
        return this;
    }

    /**
     * 进行自定义构造后续直接自定义算法，密钥，过期时间
     *
     * @param enable
     * @return
     */
    @Override
    public JQuickerBase defaultBuilder(boolean enable) {
        if (enable) {
            defaultBuilder();
        } else {
            this.defaultEnable = false;
        }
        return this;
    }

    /**
     * 构造token
     * @return
     */
    @Override
    public String createToken() {
        //记录当前时间
        USER_DATA.put("create", System.currentTimeMillis());
        String token = Jwts.builder()
                .setClaims(USER_DATA)
                .setExpiration(initExpirationTime(EXPIRATION_TIME))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSecretKey(), SIGN)
                .compact();

        return token;
    }

    /**
     * 未编码密钥处理，用户构造或解析token时传入加密密钥
     */
    public SecretKey getSecretKey() {
        //拦截，若未设置密钥则调用默认初始化密钥
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * 宽解析，得到Header，PayLoad，Signature的包装
     */
    @Override
    public Jws analysisToken(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token);
        return claimsJws;
    }

    /**
     * 判断token是否过期
     *
     * @param token
     * @return
     */
    @Override
    public boolean inspectExpirationTime(String token) {

        return analyzeExpirationOrNot(token);
    }

    @Override
    public String initSecretKey(String selfDefineSecretKey) {
        return SECRET_KEY = selfDefineSecretKey;
    }

    @Override
    public String initSecretKey(JWTDefaultSecretKey defaultSecretKey) {
        return SECRET_KEY = defaultSecretKey.getValue();

    }

    @Override
    public SignatureAlgorithm initSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        return SIGN = signatureAlgorithm;
    }

    /**
     * 用户自定义过期时间
     *
     * @param expirationTime
     * @return
     */
    @Override
    public Date initExpirationTime(JWTDefaultExpirationTime expirationTime) {
        this.EXPIRATION_TIME = expirationTime;
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, this.EXPIRATION_TIME.getValue());
        return instance.getTime();
    }

    /**
     * 设置需要存储的内容
     *
     * @param data
     * @return
     */
    @Override
    public Claims setStoreData(Map<String, String> data) {
        USER_DATA = Jwts.claims();
        USER_DATA.putAll(data);
        return USER_DATA;
    }

    /**
     * 具体解析出Header
     */
    @Override
    public JwsHeader analyzeHeader(String token) {
        Jws jws = analysisToken(token);
        return (JwsHeader) jws.getHeader();

    }

    /**
     * 具体解析出PayLoad
     */
    @Override
    public Claims analyzePayLoad(String token) {
        Jws jws = analysisToken(token);
        return (Claims) jws.getBody();

    }

    /**
     * 具体解析出Signature
     */
    @Override
    public String analyzeSignature(String token) {
        Jws jws = analysisToken(token);
        return jws.getSignature();
    }

    /**
     * 具体解析出时间（过期）
     * 表示过期那天的时间戳
     */
    @Override
    public String analyzeExpirationTime(String token) {
        Claims tokenPayLoad = analyzePayLoad(token);
        long tokenExpirationTime = Long.valueOf((Integer) tokenPayLoad.get("exp"))*1000;
        return JWTDefaultExpirationTime.getTokenExpirationTimeStamp(tokenExpirationTime);
    }

    /**
     * 判断是否过期，和当前时间戳相比
     * @param token
     * @return
     */
    @Override
    public boolean analyzeExpirationOrNot(String token) {
        Claims tokenPayLoad = analyzePayLoad(token);
        long tokenExpirationTime = Long.valueOf((Integer) tokenPayLoad.get("exp"))*1000;
        long rest = System.currentTimeMillis() - tokenExpirationTime;
        return rest >= 0;
    }

    /**
     * 获取还剩下多长时间过期
     * @return
     */
    public int getRestTime(String token){
        Claims tokenPayLoad = analyzePayLoad(token);
        long tokenExpirationTime = Long.valueOf((Integer) tokenPayLoad.get("exp"))*1000;
        Long rest = System.currentTimeMillis() - tokenExpirationTime;
        return rest.intValue();
    }

    /**
     * 获取还剩下多长时间过期 单位任意取决与你的传入
     * @param token
     * @param timeUnit
     * @return
     */
    public int getRestTime(String token, JWTDefaultExpirationTime.TimeUnit timeUnit){
        Claims tokenPayLoad = analyzePayLoad(token);
        long tokenExpirationTime = Long.valueOf((Integer) tokenPayLoad.get("exp"))*1000;
        Long rest =  tokenExpirationTime - System.currentTimeMillis() ;
        final val number = JWTDefaultExpirationTime.formatTime(rest, timeUnit);
        return number.intValue();
    }
}
