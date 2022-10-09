package com.q.reminder.reminder.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.CoverityVo;

import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson2.JSONReader.Feature.IgnoreNoneSerializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.CoverityApi
 * @Description :
 * @date :  2022.10.09 09:12
 */
public class CoverityApi {
    private final static String URL = "http://192.168.2.39:8080/reports/table.json?projectId=%d&viewId=%d";

    /**
     * 通过cookie、project、view 查询coverity中、高问题集合
     * @param cookie
     * @param projectId
     * @param viewId
     * @return
     */
    public static List<CoverityVo> queryHightMidQ(String cookie, Integer projectId, Integer viewId) {
        String res = HttpUtil.createGet(String.format(URL, projectId, viewId)).addHeaders(Map.of("Cookie", "COVJSESSIONID8080PI=" + cookie)).execute().body();
        JSONObject resultSet = JSON.parseObject(res);
        JSONObject result = resultSet.getJSONObject("resultSet");
        return result.getList("results", CoverityVo.class, IgnoreNoneSerializable);
    }
}
