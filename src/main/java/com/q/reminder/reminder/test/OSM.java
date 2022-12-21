package com.q.reminder.reminder.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.test.OSM
 * @Description :
 * @date :  2022.12.20 15:39
 */
public class OSM {

    public static void main(String[] args) {
        String clmInfo = getClmInfo("01-Nov-2022", "10-Dec-2022");
        System.out.println(clmInfo);
    }


    /**
     * 获取Token
     *
     * @return String
     * @throws Exception
     */
    private static String token() {
        String accessToken = "access_token";
        String url = "https://corp.sts.ford.com/adfs/oauth2/token";
        JSONObject params = new JSONObject();
        params.put("response_type", "token");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "20fa99c7-9db5-af03-46ba-2a577f0d2e92");
        params.put("client_secret", "OVerYQ_QMZ-7EqYKAQS7ty_ujrQE1svLsM5DsBys");
        params.put("resource", "urn:aws:resource:web_awsexternalperl:prod");
        String post;
        try {
            post = HttpUtil.post(url, params);
        } catch (HttpException e) {
            return null;
        }
        if (StringUtils.isBlank(post) && !post.contains(accessToken)) {
            return null;
        }
        return "Bearer " + JSONObject.parseObject(post).get(accessToken).toString();
    }

    /**
     * 接口调用
     *
     * @param sDate     开始日期
     * @param eDate     结束日期
     * @return String
     * @throws Exception 异常处理
     */
    private static String getClmInfo(String sDate, String eDate) {
        String url = String.format("https://www.gsar.ford.com/qr/be/api/dcsp/getClmInfo?sdate=%s&edate=%s", sDate, eDate);
        HttpResponse execute = null;
        String token = token();
        if (StringUtils.isBlank(token)) {
            return null;
        }
        try {
            execute = HttpRequest.get(url).header("Authorization", token).timeout(30 * 1000 * 60).execute();
        } catch (HttpException e) {

        }
        if (execute != null) {
            return execute.body();
        }
        return null;
    }
}
