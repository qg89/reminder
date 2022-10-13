package com.q.reminder.reminder.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.CoverityVo;
import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson2.JSONReader.Feature.IgnoreNoneSerializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.CoverityApi
 * @Description : coverity所用API
 * @date :  2022.10.09 09:12
 */
@Log4j2
public class CoverityApi {
    private final static String URL = "http://192.168.2.39:8080/reports/table.json?projectId=%d&viewId=%d";

    /**
     * 通过cookie、project、view 查询coverity中、高问题集合
     *
     * @param cookie    通过浏览器查看cookie
     * @param projectId 项目ID
     * @param viewId    视图ID
     * @return 组装数据
     */
    public static List<CoverityVo> queryHightMidQ(String cookie, Integer projectId, Integer viewId) {
        String res = HttpUtil.createGet(String.format(URL, projectId, viewId)).addHeaders(Map.of("Cookie", "COVJSESSIONID8080PI=" + cookie)).execute().body();
        JSONObject resultSet;
        try {
            resultSet = JSON.parseObject(res);
        } catch (Exception e) {
            log.error("请求coverity 返回数据结构转换异常", e);
            return null;
        }
        JSONObject result = resultSet.getJSONObject("resultSet");
        return result.getList("results", CoverityVo.class, IgnoreNoneSerializable);
    }

    /**
     * 通过cookie读取coverity任务
     *
     * @param vo coverity 所用项目、视图
     */
    public static List<CoverityAndRedmineSaveTaskVo> readCoverity(CoverityAndRedmineSaveTaskVo vo) {
        List<CoverityVo> coverityVoList = CoverityApi.queryHightMidQ("E6E6E8432545DE9FB6A106BA6B47AB98", vo.getCoverityProjectId(), vo.getViewId());
        if (coverityVoList == null || coverityVoList.isEmpty()) {
            log.info("coverity 返回结果为空");
            return new ArrayList<>(0);
        }
        List<CoverityAndRedmineSaveTaskVo> resultList = new ArrayList<>();
        coverityVoList.forEach(e -> {
            String displayType = e.getDisplayType();
            Integer cId = e.getCid();
            String content = "类型:" + displayType + "," + "CID:" + cId + "\r\n" +
                    "类别:" + e.getDisplayCategory() + "\r\n" +
                    "文件路径:" + e.getDisplayFile() + "\r\n" +
                    "行数:" + e.getLineNumber();
            String subject = "CID:[" + cId + "]-" + displayType;
            vo.setSubject(subject);
            vo.setDescription(content);
            vo.setCid(cId);
            resultList.add(vo);
        });
        return resultList;
    }
}
