package com.q.reminder.reminder.handle;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.vo.SheetVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.SyncRedmineTaskHandle
 * @Description :
 * @date :  2022.10.27 10:56
 */
@Log4j2
@Component
public class SyncRedmineTaskHandle {
    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;

    @Autowired
    private ProjectInfoService projectInfoService;

    @Scheduled(cron = "0/20 * * * * ?")
    public void syncRedmineTask() {
        // 查询项目对应的需求管理表token
        List<ProjectInfo> projectInfos = projectInfoService.list();
        projectInfos.forEach(e -> {
            String pName = e.getPName();
            String pId = e.getPId();
            String pKey = e.getPKey();
            String featureToken = e.getFeatureToken();
            // 获取各项目中需求管理表中sheetId
            String secret = FeiShuApi.getSecret(appId, appSecret);
            List<SheetVo> sheeList = FeiShuApi.getSpredsheets(featureToken, secret);

            // 获取飞书文档中需求ID为空的数据
            List<JSONObject> ranges = FeiShuApi.getRanges(featureToken, "", secret);
            for (JSONObject rangeJson : ranges) {
                String range = rangeJson.getString("range");
                JSONArray valuesJson = rangeJson.getJSONArray("values");
            }

            // 构建redmine发送任务的实体集合
            // RedmineApi.createTask();

            // 同步更新需求管理表featureId
            FeiShuApi.updateRange(featureToken, secret, "", "");
        });
    }
}
