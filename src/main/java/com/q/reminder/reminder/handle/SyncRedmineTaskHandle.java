package com.q.reminder.reminder.handle;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.service.AdminInfoService;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.DefinitionVo;
import com.q.reminder.reminder.vo.FeatureListVo;
import com.q.reminder.reminder.vo.RedmineVo;
import com.q.reminder.reminder.vo.SheetVo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
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
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private RedmineUserInfoService redmineUserInfoService;
    @Autowired
    private AdminInfoService adminInfoService;
    @Autowired
    private FeishuProperties feishuProperties;

    @XxlJob("syncRedmineTask")
    public void syncRedmineTask() {
        // 查询项目对应的需求管理表token
        LambdaQueryWrapper<ProjectInfo> lq = new LambdaQueryWrapper<>();
        lq.isNotNull(ProjectInfo::getFeatureToken);
        List<ProjectInfo> projectInfos = projectInfoService.list(lq);
        Map<String, Integer> redmineUserMap = redmineUserInfoService.list(Wrappers.<RedmineUserInfo>lambdaQuery().isNotNull(RedmineUserInfo::getAssigneeName)).stream().collect(Collectors.toMap(e -> e.getAssigneeName().replace(" ", ""), RedmineUserInfo::getAssigneeId));
        List<AdminInfo> adminInfoList = adminInfoService.list();
        String secret = FeiShuApi.getSecret(feishuProperties.getAppId(), feishuProperties.getAppSecret());
        projectInfos.forEach(projectInfo -> {
            String pKey = projectInfo.getPKey();
            String pId = projectInfo.getPId();
            String featureToken = projectInfo.getFeatureToken();
            String redmineUrl = projectInfo.getRedmineUrl();
            String apiAccessKey = projectInfo.getAccessKey();
            // 获取各项目中需求管理表中sheetId和sheet名称
            List<SheetVo> sheetList = FeiShuApi.getSpredsheets(featureToken, secret);
            StringBuilder ranges = new StringBuilder();
            String featureRange = "";
            String definitionRange = "";
            DefinitionVo definition = new DefinitionVo();
            definition.setApiAccessKey(apiAccessKey);
            definition.setRedmineUrl(redmineUrl);
            definition.setProjectId(Integer.valueOf(pId));
            for (SheetVo sheetVo : sheetList) {
                String sheetId = sheetVo.getSheetId();
                String title = sheetVo.getTitle();
                if ("需求管理表".equals(title)) {
                    featureRange = (sheetId + "!A1:AZ2000");
                    ranges.append(featureRange).append(",");
                    continue;
                }
                if ("定义".equals(title)) {
                    definitionRange = (sheetId + "!A1:AZ3");
                    ranges.append(definitionRange);
                }
            }
            // 获取飞书文档中需求ID为空的数据
            List<JSONObject> rangeList = FeiShuApi.getRanges(featureToken, ranges.toString(), secret);
            List<FeatureListVo> featureList = new ArrayList<>();

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
            RedmineApi.createTask(featureList, definition, redmineUserMap, pKey);

            List<RedmineVo> redmineVos = new ArrayList<>();
            featureList.forEach(feature -> {
                String featureId = feature.getFeatureId();
                String range = feature.getRange();
                if (StringUtils.isBlank(featureId)) {
                    log.info("该任务已存在,不再重新新增任务");
                    return;
                }
                RedmineVo vo = new RedmineVo();
                vo.setRedmineId(feature.getRedmineId());
                vo.setSubject(feature.getRedmineSubject());
                redmineVos.add(vo);
                // 同步更新需求管理表featureId
                FeiShuApi.updateRange(featureToken, secret, range, featureId);
            });

            String isSendGroup = projectInfo.getIsSendGroup();
            String sendGroupChatId = projectInfo.getSendGroupChatId();
            String productMemberId = projectInfo.getProductMemberId();
            if ("0".equals(isSendGroup) && StringUtils.isNotBlank(sendGroupChatId) && StringUtils.isNotBlank(productMemberId) && !CollectionUtils.isEmpty(redmineVos)) {
                JSONObject content = new JSONObject();
                JSONObject all = new JSONObject();
                all.put("title", "新增需求如下:");
                JSONArray contentJsonArray = new JSONArray();
                JSONArray subContentJsonArray = new JSONArray();
                JSONObject at = new JSONObject();
                at.put("tag", "at");
                at.put("user_id", productMemberId);
                at.put("user_name", definition.getProduct());
                subContentJsonArray.add(at);

                for (RedmineVo redmineVo : redmineVos) {
                    JSONObject a = new JSONObject();
                    a.put("tag", "a");
                    a.put("href", redmineUrl + "/issues/" + redmineVo.getRedmineId());
                    a.put("text", "\r\n" + redmineVo.getSubject());
                    subContentJsonArray.add(a);
                }

                contentJsonArray.add(subContentJsonArray);
                all.put("content", contentJsonArray);
                content.put("zh_cn", all);
                try {
                    FeiShuApi.sendGroupByChats(sendGroupChatId, content.toJSONString(), secret);
                } catch (IOException e) {
                    FeiShuApi.sendAdmin(adminInfoList, "发送飞书需求群异常!", secret);
                }
            }
        });
    }
}
