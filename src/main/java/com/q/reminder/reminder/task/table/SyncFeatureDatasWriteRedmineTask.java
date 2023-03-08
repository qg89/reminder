package com.q.reminder.reminder.task.table;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.q.reminder.reminder.config.RedmineConfig;
import com.q.reminder.reminder.constant.TableTypeContants;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.entity.TTableUserConfig;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.TTableFeatureTmpService;
import com.q.reminder.reminder.service.TTableInfoService;
import com.q.reminder.reminder.service.TTableUserConfigService;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineProcessingException;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.Transport;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncTableRecordSampleTask
 * @Description : 同步多维度表格记录-需求管理表写入redmine
 * @date :  2023.01.17 11:40
 */
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

    private Date dueDate = DateTime.now().plusDays(7).toDate();

    @XxlJob("syncTableRecordTask")
    public void syncTableRecordTask() throws Exception {
        LambdaQueryWrapper<TTableFeatureTmp> tableQw = Wrappers.lambdaQuery();
        tableQw.eq(TTableFeatureTmp::getWriteRedmine, "0");
//        tableQw.gtSql(TTableFeatureTmp::getUpdateTime, " date_sub( NOW(), INTERVAL 10 MINUTE)");
        tableQw.eq(TTableFeatureTmp::getWriteType, "是");
        List<TTableFeatureTmp> featureDataList = tTableFeatureTmpService.list(tableQw);
        Map<String, TTableUserConfig> userConfigMap = tTableUserConfigService.list().stream().collect(Collectors.toMap(TTableUserConfig::getPrjctKey, Function.identity(), (v1, v2) -> v1));
        Map<String, RProjectInfo> projectMap = projectInfoService.listAll().stream().collect(Collectors.toMap(e -> String.valueOf(e.getId()), Function.identity(), (v1, v2) -> v1));

        List<AppTableRecord> records = new ArrayList<>();

        // key: pkey, value redmineType
        Map<String, String> redmineTypeMap = projectInfoService.listAll().stream().collect(Collectors.toMap(RProjectInfo::getPkey, RProjectInfo::getRedmineType));

        for (TTableFeatureTmp featureTmp : featureDataList) {
            String recordsId = featureTmp.getRecordsId();
            String prjctKey = featureTmp.getPrjctKey();
            String mdl = featureTmp.getMdl();
            String menuOne = featureTmp.getMenuOne();
            String menuTwo = featureTmp.getMenuTwo();
            String menuThree = featureTmp.getMenuThree();
            String dscrptn = featureTmp.getDscrptn();
            Float prdct = featureTmp.getPrdct();
            LocalDate prodTime = featureTmp.getProdTime();
            String featureType = featureTmp.getFeatureType();
            RedmineConfig type = RedmineConfig.type(redmineTypeMap.get(prjctKey));

            boolean ftrTyp = "非功能".equals(featureType);

            TTableUserConfig config = userConfigMap.get(prjctKey);
            RProjectInfo RProjectInfo = projectMap.get(config.getPId().toString());
            Integer pId = Integer.valueOf(RProjectInfo.getPid());

            Transport transport = RedmineApi.getTransportByProject(RProjectInfo);
            if (RedmineApi.checkIssue(transport, recordsId)) {
                continue;
            }
            StringBuilder subject = new StringBuilder();
            if (StringUtils.isNotBlank(mdl)) {
                subject.append("模块：").append(mdl).append("-");
            }
            if (StringUtils.isNotBlank(menuOne)) {
                subject.append("一级：").append(menuOne).append("-");
            }
            if (StringUtils.isNotBlank(menuTwo)) {
                subject.append("二级：").append(menuTwo).append("-");
            }
            if (StringUtils.isNotBlank(menuThree)) {
                subject.append("三级：").append(menuThree);
            }
            int lastChar = subject.lastIndexOf("-");
            if (lastChar == subject.length() - 1) {
                subject.deleteCharAt(lastChar);
            }
            Issue issue = new Issue();
            issue.setSubject(subject.toString());
            issue.setDescription(dscrptn);
            issue.setAssigneeId(config.getPrdctId());
            issue.setDueDate(dueDate);
            if (prodTime != null) {
                dueDate = Date.from(prodTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                issue.setDueDate(dueDate);
            }
            issue.setSpentHours(prdct);
            issue.setProjectId(pId);
            CustomField customField = type.setCustomValue(recordsId);
            List<CustomField> customFieldList = new ArrayList<>(RedmineConfig.CUSTOM_FIELDS);
            customFieldList.add(customField);
            issue.addCustomFields(customFieldList);
            Issue parentIssue = new Issue();
            if (ftrTyp) {
                issue.setStatusId(1).setCreatedOn(new Date());
                issue.setTracker(RedmineConfig.DEV_TRACKER);
                if (!createSubIssue(issue, featureTmp, config, transport, customFieldList, ftrTyp)) {
                    featureTmp.setWriteRedmine("3");
                }
            } else {
                issue.setTracker(RedmineConfig.FEATURE_TRACKER);
                try {
                    parentIssue = RedmineApi.createIssue(issue, transport);
                } catch (RedmineProcessingException e) {
                    List<String> errors = e.getErrors();
                    log.error("[多维表格-创建redmine任务]父任务异常：{}", errors);
                }
                featureTmp.setWriteRedmine("1");
                if (parentIssue.getId() == null) {
                    featureTmp.setWriteRedmine("2");
                } else if (!createSubIssue(parentIssue, featureTmp, config, transport, customFieldList, ftrTyp)) {
                    featureTmp.setWriteRedmine("3");
                }
            }

            tTableFeatureTmpService.updateById(featureTmp);
            if ("1".equals(featureTmp.getWriteRedmine())) {
                records.add(AppTableRecord.newBuilder().recordId(recordsId).fields(Map.of("需求ID", recordsId)).build());
            }
        }

        LambdaQueryWrapper<TTableInfo> lq = Wrappers.lambdaQuery();
        lq.eq(TTableInfo::getTableType, TableTypeContants.FEATURE);
        TTableInfo tTableInfo = tTableInfoService.getOne(lq);
        if (!CollectionUtils.isEmpty(records)) {
            BaseFeishu.table(client).batchUpdateTableRecords(tTableInfo, records.toArray(new AppTableRecord[0]));
        }

        if (DateUtil.dayOfWeek(new Date()) == 1) {
            LambdaQueryWrapper<TTableFeatureTmp> query = Wrappers.lambdaQuery();
            query.eq(TTableFeatureTmp::getWriteRedmine, "1");
            List<TTableFeatureTmp> tempList = tTableFeatureTmpService.list(query);
            tTableFeatureTmpService.removeBatchByIds(tempList);
        }
    }

    /**
     * 创建子任务
     *
     * @param parentIssue
     * @param featureTmp
     * @param config
     * @param transport
     * @param customFields
     * @param ftrTyp
     * @throws RedmineException
     */
    private boolean createSubIssue(Issue parentIssue, TTableFeatureTmp featureTmp, TTableUserConfig config, Transport transport, List<CustomField> customFields, boolean ftrTyp) throws RedmineException {
        boolean createSubIssue = true;
        Integer issueId = parentIssue.getId();
        Issue issue = new Issue();
        issue.addCustomFields(customFields);
        issue.setDescription(parentIssue.getDescription());
        issue.setProjectId(parentIssue.getProjectId());
        if (!ftrTyp) {
            issue.setParentId(issueId);
        }
        issue.setTransport(transport);
        issue.setDueDate(dueDate);
        Float front = featureTmp.getFront();
        Float algrthm = featureTmp.getAlgrthm();
        Float andrd = featureTmp.getAndrd();
        Float archtct = featureTmp.getArchtct();
        Float back = featureTmp.getBack();
        Float bgdt = featureTmp.getBgdt();
        Float implmntton = featureTmp.getImplmntton();
        Float oprton = featureTmp.getOprton();
        Float test = featureTmp.getTest();
        if (front != null && createSubIssue) {
            Issue frontIssue = issue;
            frontIssue.setSubject(parentIssue.getSubject() + "-前端");
            frontIssue.setSpentHours(front);
            frontIssue.setTracker(RedmineConfig.DEV_TRACKER);
            frontIssue.setAssigneeId(config.getFrontId());
            createSubIssue = frontIssue.create().getId() != null;
        }
        if (algrthm != null && createSubIssue) {
            Issue algrthmIssue = issue;
            algrthmIssue.setSubject(parentIssue.getSubject() + "-算法");
            algrthmIssue.setSpentHours(algrthm);
            algrthmIssue.setTracker(RedmineConfig.DEV_TRACKER);
            algrthmIssue.setAssigneeId(config.getAlgrthmId());
            createSubIssue = algrthmIssue.create().getId() != null;
        }
        if (andrd != null && createSubIssue) {
            Issue andrdIssue = issue;
            andrdIssue.setSubject(parentIssue.getSubject() + "-安卓");
            andrdIssue.setSpentHours(andrd);
            andrdIssue.setTracker(RedmineConfig.DEV_TRACKER);
            issue.setAssigneeId(config.getAndrdId());
            createSubIssue = andrdIssue.create().getId() != null;
        }
        if (archtct != null && createSubIssue) {
            Issue archtctIssue = issue;
            archtctIssue.setSubject(parentIssue.getSubject() + "-架构");
            archtctIssue.setSpentHours(archtct);
            archtctIssue.setTracker(RedmineConfig.DEV_TRACKER);
            archtctIssue.setAssigneeId(config.getArchtctId());
            createSubIssue = archtctIssue.create().getId() != null;
        }
        if (back != null && createSubIssue) {
            Issue backIssue = issue;
            backIssue.setSubject(parentIssue.getSubject() + "-后端");
            backIssue.setSpentHours(back);
            backIssue.setTracker(RedmineConfig.DEV_TRACKER);
            backIssue.setAssigneeId(config.getBackId());
            createSubIssue = backIssue.create().getId() != null;
        }
        if (bgdt != null && createSubIssue) {
            Issue bgdtIssue = issue;
            bgdtIssue.setSubject(parentIssue.getSubject() + "-大数据");
            bgdtIssue.setSpentHours(bgdt);
            bgdtIssue.setTracker(RedmineConfig.DEV_TRACKER);
            bgdtIssue.setAssigneeId(config.getBgdtId());
            createSubIssue = bgdtIssue.create().getId() != null;
        }
        if (implmntton != null && createSubIssue) {
            Issue impIssue = issue;
            impIssue.setSubject(parentIssue.getSubject() + "-实施");
            impIssue.setSpentHours(implmntton);
            impIssue.setTracker(RedmineConfig.DEV_TRACKER);
            impIssue.setAssigneeId(config.getImplmnttonId());
            createSubIssue = impIssue.create().getId() != null;
        }
        if (oprton != null && createSubIssue) {
            Issue impIssue = issue;
            impIssue.setSubject(parentIssue.getSubject() + "-运维");
            impIssue.setSpentHours(oprton);
            impIssue.setTracker(RedmineConfig.DEV_TRACKER);
            impIssue.setAssigneeId(config.getOprtonId());
            createSubIssue = impIssue.create().getId() != null;
        }
        if (test != null && createSubIssue) {
            Issue testIssue = issue;
            testIssue.setSubject(parentIssue.getSubject() + "-测试用例");
            testIssue.setSpentHours(test);
            testIssue.setTracker(RedmineConfig.TEST_TRACKER);
            testIssue.setAssigneeId(config.getTestId());
            createSubIssue = testIssue.create().getId() != null;

            Issue caseIssue = issue;
            caseIssue.setSubject(parentIssue.getSubject() + "-测试执行");
            caseIssue.setSpentHours(test);
            caseIssue.setTracker(RedmineConfig.TEST_TRACKER);
            caseIssue.setAssigneeId(config.getTestId());
            createSubIssue = createSubIssue && caseIssue.create().getId() != null;
        }
        return createSubIssue;
    }
}
