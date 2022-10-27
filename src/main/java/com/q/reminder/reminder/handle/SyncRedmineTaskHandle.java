package com.q.reminder.reminder.handle;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.DefinitionVo;
import com.q.reminder.reminder.vo.FeatureListVo;
import com.q.reminder.reminder.vo.SheetVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Value("${redmine-config.old_url}")
    private String redmineOldUrl;
    @Value("${redmine-config.api-access-key.saiko}")
    private String apiAccessKeySaiko;

    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private RedmineUserInfoService redmineUserInfoService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void syncRedmineTask() {
        // 查询项目对应的需求管理表token
        LambdaQueryWrapper<ProjectInfo> lq = new LambdaQueryWrapper<>();
        lq.isNotNull(ProjectInfo::getFeatureToken);
        List<ProjectInfo> projectInfos = projectInfoService.list(lq);
        Map<String, Integer> redmineUserMap = redmineUserInfoService.list(Wrappers.<RedmineUserInfo>lambdaQuery().isNotNull(RedmineUserInfo::getAssigneeName)).stream().collect(Collectors.toMap(e -> e.getAssigneeName().replace(" ", ""), RedmineUserInfo::getAssigneeId));

        String secret = FeiShuApi.getSecret(appId, appSecret);
        projectInfos.forEach(e -> {
            String pId = e.getPId();
            String featureToken = e.getFeatureToken();
            // 获取各项目中需求管理表中sheetId和sheet名称
            List<SheetVo> sheetList = FeiShuApi.getSpredsheets(featureToken, secret);
            StringBuilder ranges = new StringBuilder();
            String featureRange = "";
            String definitionRange = "";
            for (SheetVo sheetVo : sheetList) {
                String sheetId = sheetVo.getSheetId();
                String title = sheetVo.getTitle();
                if ("需求管理表".equals(title)) {
                    featureRange = (sheetId + "!A1:AJ2000");
                    ranges.append(featureRange).append(",");
                    continue;
                }
                if ("定义".equals(title)) {
                    definitionRange = (sheetId + "!A1:AZ2");
                    ranges.append(definitionRange);
                }
            }

            // 获取飞书文档中需求ID为空的数据
            List<JSONObject> rangeList = FeiShuApi.getRanges(featureToken, ranges.toString(), secret);
            List<FeatureListVo> featureList = new ArrayList<>();
            DefinitionVo definition = new DefinitionVo();
            definition.setRedmineUrl(redmineOldUrl);
            definition.setApiAccessKey(apiAccessKeySaiko);
            definition.setProjectId(Integer.valueOf(pId));
            for (JSONObject rangeJson : rangeList) {
                String range = rangeJson.getString("range");
                JSONArray valuesJson = rangeJson.getJSONArray("values");
                List<List> lists = valuesJson.toJavaList(List.class);
                // 需求管理表
                if (StringUtils.isNotBlank(range) && range.equals(featureRange)) {
                    featureList = FeiShuApi.getFeatureList(lists, featureRange.split("!")[0]);
                }
                // 定义
                if (StringUtils.isNotBlank(range) && range.equals(definitionRange)) {
                    FeiShuApi.getDefinitionList(lists, definition);
                }
            }
            // 构建redmine发送任务的实体集合
            RedmineApi.createTask(featureList, definition, redmineUserMap);

            featureList.forEach(feature -> {
                String featureId = feature.getFeatureId();
                String range = feature.getRange();
                if (StringUtils.isBlank(featureId)) {
                    log.info("该任务已存在,不再重新新增任务");
                    return;
                }
                // 同步更新需求管理表featureId
                FeiShuApi.updateRange(featureToken, secret, range, featureId);
            });
        });
    }
}
