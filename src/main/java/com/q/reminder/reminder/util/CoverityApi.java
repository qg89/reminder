package com.q.reminder.reminder.util;

import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.vo.CoverityVo;
import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static com.alibaba.fastjson2.JSONReader.Feature.IgnoreNoneSerializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.CoverityApi
 * @Description : coverity所用API
 * @date :  2022.10.09 09:12
 */
@Log4j2
public abstract class CoverityApi {
    private static final String DOMAIN = "192.168.2.39:8080";
    private final static String LOGIN_URL = "http://" + DOMAIN;
    private final static String URL = LOGIN_URL + "/reports/table.json?projectId=%d&viewId=%d";
    private static final HashMap<String, List<Cookie>> COOKIE_STORE = new HashMap<>();


    /**
     * 通过cookie读取coverity任务
     *
     * @param vo coverity 所用项目、视图
     */
    public static CoverityAndRedmineSaveTaskVo readCoverity(CoverityAndRedmineSaveTaskVo vo) {
        OkHttpClient client = login();
        List<CoverityVo> coverityVoList = queryList(client, vo);
        if (coverityVoList.isEmpty()) {
            log.info("【Coverity】-项目名称:{} , 暂无问题！", vo.getRedmineProjectName());
            return vo;
        }
        StringBuilder content = new StringBuilder();
        coverityVoList.forEach(e -> {
            String displayType = e.getDisplayType();
            Integer cId = e.getCid();
            content.append("类型:").append(displayType).append(",").append("CID:").append(cId).append("\r\n").append("类别:").append(e.getDisplayCategory()).append("\r\n").append("文件路径:").append(e.getDisplayFile()).append("\r\n").append("行数:").append(e.getLineNumber()).append("\r\n\t");
        });
        vo.setCoverityNo(coverityVoList.size());
        vo.setDescription(content.toString());
        return vo;
    }


    private static String getCacheKey(HttpUrl url) {
        return url.host() + ":" + url.port();
    }

    public static OkHttpClient login() {
        RequestBody body = new FormBody.Builder()
                .add("username", "d3")
                .add("password", "t6AnB7")
                .add("_csrf", UUID.randomUUID().toString())
                .build();
        Headers.Builder builder = new Headers.Builder();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> cookies) {
                        if (cookies.size() > 0) {
                            COOKIE_STORE.put(getCacheKey(httpUrl), cookies);
                        }
                    }

                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        List<Cookie> cookies = COOKIE_STORE.get(getCacheKey(httpUrl));
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();

        Request post = new Request.Builder()
                .url(LOGIN_URL + "/login")
                .method("POST", body)
                .headers(builder.build())
                .build();

        try {
            client.newCall(post).execute();
            return client;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return client;
    }

    static List<CoverityVo> queryList(OkHttpClient client, CoverityAndRedmineSaveTaskVo vo) {
        String cookie = "COVJSESSIONID8080PI=" + COOKIE_STORE.get(DOMAIN).get(0).value();
        Request get = new Request.Builder()
                .url(String.format(URL, vo.getCoverityProjectId(), vo.getViewId()))
                .header("Cookie", cookie)
                .header("Host", DOMAIN)
                .get()
                .build();
        String result = "";
        try (Response response = client.newCall(get).execute();){
            result = Objects.requireNonNull(response.body()).string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject resultJson = JSONObject.parseObject(result).getJSONObject("resultSet");
        List<CoverityVo> resultList = resultJson.getJSONArray("results").toJavaList(CoverityVo.class, IgnoreNoneSerializable);
        if (resultList != null) {
            return resultList;
        }
        return new ArrayList<>();
    }
}
