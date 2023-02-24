package com.q.reminder.reminder.task;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.Client;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.service.AdminInfoService;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.DefinitionVo;
import com.q.reminder.reminder.vo.FeatureListVo;
import com.q.reminder.reminder.vo.RedmineVo;
import com.q.reminder.reminder.vo.SheetVo;
import com.xxl.job.core.biz.model.ReturnT;
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
@Deprecated
@Log4j2
@Component
public class SyncRedmineTask {
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private RedmineUserInfoService redmineUserInfoService;
    @Autowired
    private AdminInfoService adminInfoService;
    @Autowired
    private FeishuProperties feishuProperties;
    @Autowired
    private Client client;

    @Deprecated
    @XxlJob("syncRedmineTask")
    public ReturnT<String> syncRedmineTask() {
        ReturnT<String> r = new ReturnT<>(null);
        // 查询项目对应的需求管理表token
        LambdaQueryWrapper<RProjectInfo> lq = new LambdaQueryWrapper<>();
        lq.isNotNull(RProjectInfo::getFeatureToken);
        lq.isNotNull(RProjectInfo::getPmKey);
        lq.eq(RProjectInfo::getSyncFeature, "0");
        List<RProjectInfo> projectInfoList = projectInfoService.list(lq);
        Map<String, Integer> redmineUserMap = redmineUserInfoService.list(Wrappers.<RedmineUserInfo>lambdaQuery().isNotNull(RedmineUserInfo::getAssigneeName)).stream().collect(Collectors.toMap(e -> e.getAssigneeName().replace(" ", "") + "-" + e.getRedmineType(), RedmineUserInfo::getAssigneeId, (v1, v2) -> v1));
        List<AdminInfo> adminInfoList = adminInfoService.list();
        String secret = FeiShuApi.getSecret(feishuProperties.getAppId(), feishuProperties.getAppSecret());
        StringBuilder conten = new StringBuilder();
        projectInfoList.forEach(projectInfo -> {
            String pKey = projectInfo.getPKey();
            String pId = projectInfo.getPId();
            String featureToken = projectInfo.getFeatureToken();
            String redmineUrl = projectInfo.getRedmineUrl();
            String pmKey = projectInfo.getPmKey();
            String pName = projectInfo.getPName();
            String redmineType = projectInfo.getRedmineType();
            // 获取各项目中需求管理表中sheetId和sheet名称
            List<SheetVo> sheetList = null;
            try {
                sheetList = BaseFeishu.cloud(client).getSpredsheets(featureToken);
            } catch (Exception e) {
                log.error(e);
            }
            if (CollectionUtils.isEmpty(sheetList)) {
                conten.append(r.getMsg()).append("|").append(this.getClass().getName()).append(" 获取需求管理表为空").append(" 项目：").append(pName);
                r.setMsg(conten.toString());
                return;
            }
            StringBuilder ranges = new StringBuilder();
            String featureRange = "";
            String definitionRange = "";
            DefinitionVo definition = new DefinitionVo();
            definition.setApiAccessKey(pmKey);
            definition.setRedmineUrl(redmineUrl);
            definition.setProjectId(Integer.valueOf(pId));
            definition.setRedmineType(redmineType);
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
                    log.info("redmine生成featureId失败");
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
        return r;
    }
}
