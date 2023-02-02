package com.q.reminder.reminder.task.table;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.q.reminder.reminder.constant.TableTypeContants;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.entity.TTableUserConfig;
import com.q.reminder.reminder.enums.CustomFieldsEnum;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.TTableFeatureTmpService;
import com.q.reminder.reminder.service.TTableInfoService;
import com.q.reminder.reminder.service.TTableUserConfigService;
import com.q.reminder.reminder.util.FeishuJavaUtils;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.Transport;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncTableRecordSampleTask
 * @Description : 同步多维度表格记录-需求管理表写入redmine
 * @date :  2023.01.17 11:40
 */
@Deprecated
@Log4j2
@Component
public class SyncFeatureDatasWriteRedmineTask {
    @Autowired
    private Client client;
    @Autowired
    private TTableFeatureTmpService tTableFeatureTmpService;
    @Autowired
    private TTableInfoService tTableInfoService;
    @Autowired
    private TTableUserConfigService tTableUserConfigService;
    @Autowired
    private ProjectInfoService projectInfoService;

    private List<CustomField> CUSTOM_FIELD_LIST = new ArrayList<>(List.of(
            new CustomField().setId(CustomFieldsEnum.FEATURE_TYPE.getId()).setName(CustomFieldsEnum.FEATURE_TYPE.getName()).setValue("功能"),
            new CustomField().setId(CustomFieldsEnum.REQUIRE_VALIDATION.getId()).setName(CustomFieldsEnum.REQUIRE_VALIDATION.getName()).setValue("是")
    ));
    private Tracker FEATURE_TRACKER = new Tracker().setId(2).setName("需求");
    private Tracker DEV_TRACKER = new Tracker().setId(7).setName("开发");
    private Tracker TEST_TRACKER = new Tracker().setId(8).setName("测试");

    @XxlJob("syncTableRecordTask")
    public void syncTableRecordTask() throws Exception {
        LambdaQueryWrapper<TTableFeatureTmp> tableQw = Wrappers.lambdaQuery();
        tableQw.eq(TTableFeatureTmp::getWriteRedmine, "0");
        tableQw.gtSql(TTableFeatureTmp::getUpdateTime, " date_sub( NOW(), INTERVAL 10 MINUTE)");
        List<TTableFeatureTmp> featureDataList = tTableFeatureTmpService.list(tableQw);
        Map<String, TTableUserConfig> userConfigMap = tTableUserConfigService.list().stream().collect(Collectors.toMap(TTableUserConfig::getPrdctName, Function.identity(), (v1, v2) -> v1));
        Map<String, ProjectInfo> projectMap = projectInfoService.list().stream().collect(Collectors.toMap(e -> String.valueOf(e.getId()), Function.identity(), (v1, v2) -> v1));

        List<AppTableRecord> records = new ArrayList<>();

        for (TTableFeatureTmp featureTmp : featureDataList) {
            String featureId = IdWorker.get32UUID().substring(22);
            String projectName = featureTmp.getPrjct();
            String mdl = featureTmp.getMdl();
            String menuOne = featureTmp.getMenuOne();
            String menuTwo = featureTmp.getMenuTwo();
            String menuThree = featureTmp.getMenuThree();
            String dscrptn = featureTmp.getDscrptn();
            Float prdct = featureTmp.getPrdct();

            TTableUserConfig config = userConfigMap.get(projectName);
            ProjectInfo projectInfo = projectMap.get(config.getPId().toString());
            Integer pId = Integer.valueOf(projectInfo.getPId());

            Transport transport = RedmineApi.getTransportByProject(projectInfo);
            StringBuilder subject = new StringBuilder();
            if (StringUtils.isNotBlank(mdl)) {
                subject.append("模块：").append(mdl).append("-");
            }
            if (StringUtils.isNotBlank(menuOne)) {
                subject.append("一级：").append(menuOne);
            }
            if (StringUtils.isNotBlank(menuTwo)) {
                subject.append("-").append("二级：").append(menuTwo);
            }
            if (StringUtils.isNotBlank(menuThree)) {
                subject.append("-").append("三级：").append(menuThree);
            }
            Issue issue = new Issue();
            issue.setSubject(subject.toString());
            issue.setDescription(dscrptn);
            issue.setAssigneeId(config.getPrdctId());
            issue.setDueDate(new Date());
            issue.setSpentHours(prdct);
            issue.setProjectId(pId);

            issue.setTracker(FEATURE_TRACKER);

            CUSTOM_FIELD_LIST.add(new CustomField().setName(CustomFieldsEnum.FEATURE_ID.getName()).setId(CustomFieldsEnum.FEATURE_ID.getId()).setValue(featureId));
            issue.addCustomFields(CUSTOM_FIELD_LIST);

            Issue parentIssue = RedmineApi.createIssue(issue, transport);
            // 创建子任务
            createSubIssue(parentIssue, featureTmp, config, transport);
            if (StringUtils.isBlank(featureId)) {
                continue;
            }
            Map<String, Object> data = new HashMap<>(2);
            data.put("需求ID", featureId);
            records.add(AppTableRecord.newBuilder().recordId(featureTmp.getRecordsId()).fields(data).build());
            featureTmp.setWriteRedmine("1");
            featureTmp.setFeatureId(featureId);
            tTableFeatureTmpService.updateById(featureTmp);
        }

        LambdaQueryWrapper<TTableInfo> lq = Wrappers.lambdaQuery();
        lq.eq(TTableInfo::getTableType, TableTypeContants.FEATURE);
        TTableInfo tTableInfo = tTableInfoService.getOne(lq);
        FeishuJavaUtils.batchUpdateTableRecords(client, tTableInfo, records.toArray(new AppTableRecord[records.size()]));

        if (DateUtil.dayOfWeek(new Date()) == 1) {
            LambdaQueryWrapper<TTableFeatureTmp> query = Wrappers.lambdaQuery();
            query.eq(TTableFeatureTmp::getWriteRedmine, "1");
            List<TTableFeatureTmp> tempList = tTableFeatureTmpService.list(query);
            tTableFeatureTmpService.removeBatchByIds(tempList);
        }
    }

    /**
     * 创建子任务
     * @param parentIssue
     * @param featureTmp
     * @param config
     * @param transport
     * @throws RedmineException
     */
    private void createSubIssue(Issue parentIssue, TTableFeatureTmp featureTmp, TTableUserConfig config, Transport transport) throws RedmineException {
        Integer issueId = parentIssue.getId();
        Issue issue = new Issue();
        issue.setDescription(parentIssue.getDescription());
        issue.setDueDate(DateTime.now().plusDays(7).toDate());
        issue.setProjectId(parentIssue.getProjectId());
        issue.setParentId(issueId);
        issue.setTransport(transport);
        Float front = featureTmp.getFront();
        Float algrthm = featureTmp.getAlgrthm();
        Float andrd = featureTmp.getAndrd();
        Float archtct = featureTmp.getArchtct();
        Float back = featureTmp.getBack();
        Float bgdt = featureTmp.getBgdt();
        Float implmntton = featureTmp.getImplmntton();
        Float oprton = featureTmp.getOprton();
        Float test = featureTmp.getTest();
        if (front != null) {
            Issue frontIssue = issue;
            frontIssue.setSubject(parentIssue.getSubject() + "-前端");
            frontIssue.setSpentHours(front);
            frontIssue.setTracker(DEV_TRACKER);
            frontIssue.setAssigneeId(config.getFrontId());
            frontIssue.create();
        }
        if (algrthm != null) {
            Issue algrthmIssue = issue;
            algrthmIssue.setSubject(parentIssue.getSubject() + "-算法");
            algrthmIssue.setSpentHours(algrthm);
            algrthmIssue.setTracker(DEV_TRACKER);
            algrthmIssue.setAssigneeId(config.getAlgrthmId());
            algrthmIssue.create();
        }
        if (andrd != null) {
            Issue andrdIssue = issue;
            andrdIssue.setSubject(parentIssue.getSubject() + "-安卓");
            andrdIssue.setSpentHours(andrd);
            andrdIssue.setTracker(DEV_TRACKER);
//            issue.setAssigneeId(config.getAlgrthmId());
//            andrdIssue.create();
        }
        if (archtct != null) {
            Issue archtctIssue = issue;
            archtctIssue.setSubject(parentIssue.getSubject() + "-架构");
            archtctIssue.setSpentHours(archtct);
            archtctIssue.setTracker(DEV_TRACKER);
            archtctIssue.setAssigneeId(config.getArchtctId());
            archtctIssue.create();
        }
        if (back != null) {
            Issue backIssue = issue;
            backIssue.setSubject(parentIssue.getSubject() + "-后端");
            backIssue.setSpentHours(back);
            backIssue.setTracker(DEV_TRACKER);
            backIssue.setAssigneeId(config.getBackId());
            backIssue.create();
        }
        if (bgdt != null) {
            Issue bgdtIssue = issue;
            bgdtIssue.setSubject(parentIssue.getSubject() + "-大数据");
            bgdtIssue.setSpentHours(bgdt);
            bgdtIssue.setTracker(DEV_TRACKER);
            bgdtIssue.setAssigneeId(config.getBgdtId());
            bgdtIssue.create();
        }
        if (implmntton != null) {
            Issue impIssue = issue;
            impIssue.setSubject(parentIssue.getSubject() + "-实施");
            impIssue.setSpentHours(implmntton);
            impIssue.setTracker(DEV_TRACKER);
            impIssue.setAssigneeId(config.getImplmnttonId());
            impIssue.create();
        }
        if (oprton != null) {
            Issue impIssue = issue;
            impIssue.setSubject(parentIssue.getSubject() + "-运维");
            impIssue.setSpentHours(oprton);
            impIssue.setTracker(DEV_TRACKER);
            impIssue.setAssigneeId(config.getOprtonId());
            impIssue.create();
        }
        if (test != null) {
            Issue testIssue = issue;
            testIssue.setSubject(parentIssue.getSubject() + "-测试");
            testIssue.setSpentHours(test);
            testIssue.setTracker(TEST_TRACKER);
            testIssue.setAssigneeId(config.getTestId());
            testIssue.create();
        }
    }
}
