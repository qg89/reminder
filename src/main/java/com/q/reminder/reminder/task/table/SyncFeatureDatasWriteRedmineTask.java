package com.q.reminder.reminder.task.table;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.q.reminder.reminder.vo.FeautreTimeVo;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineProcessingException;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.Transport;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

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
@Component
public class SyncFeatureDatasWriteRedmineTask implements BasicProcessor {
    @Autowired
    private TTableFeatureTmpService tTableFeatureTmpService;
    @Autowired
    private TTableInfoService tTableInfoService;
    @Autowired
    private TTableUserConfigService tTableUserConfigService;
    @Autowired
    private ProjectInfoService projectInfoService;

    private Date dueDate = DateTime.now().plusDays(7).toDate();


    /**
     * 创建子任务
     *
     * @param transport
     * @param customFields
     * @param ftrType
     * @param recordsId
     * @throws RedmineException
     */
    private boolean createSubIssue(Issue parentIssue, Transport transport, List<CustomField> customFields, boolean ftrType, String recordsId) throws RedmineException {
        boolean createSubIssue = true;
        Integer issueId = parentIssue.getId();

        Issue issue = new Issue();
        if (!ftrType) {
            issue.setParentId(issueId);
        }
        issue.addCustomFields(customFields);
        issue.setDescription(parentIssue.getDescription());
        issue.setProjectId(parentIssue.getProjectId());
        String subject = parentIssue.getSubject();
        issue.setTransport(transport);
        issue.setDueDate(dueDate);

        List<FeautreTimeVo> li = tTableFeatureTmpService.queryTimes(recordsId);
        for (FeautreTimeVo e : li) {
            String name = e.getName();
            Float times = e.getTimes();
            Integer id = e.getId();
            Issue newIssue = issue;
            if ("test".equals(name)) {
                newIssue.setSubject(subject + "-测试用例");
                newIssue.setSpentHours(times);
                newIssue.setTracker(RedmineConfig.TEST_TRACKER);
                newIssue.setAssigneeId(id);
                createSubIssue = newIssue.create().getId() != null;

                newIssue.setSubject(subject + "-测试执行");
                createSubIssue = createSubIssue && newIssue.create().getId() != null;
            } else {
                newIssue.setSubject(subject + "-" + ROLE_MAP.get(name));
                newIssue.setSpentHours(times);
                newIssue.setTracker(RedmineConfig.DEV_TRACKER);
                newIssue.setAssigneeId(id);
                try {
                    createSubIssue = newIssue.create().getId() != null;
                } catch (RedmineException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return createSubIssue;
    }

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger log = context.getOmsLogger();
        LambdaQueryWrapper<TTableFeatureTmp> tableQw = Wrappers.lambdaQuery();
        tableQw.eq(TTableFeatureTmp::getWriteRedmine, "0");
//        tableQw.gtSql(TTableFeatureTmp::getUpdateTime, " date_sub( NOW(), INTERVAL 10 MINUTE)");
        tableQw.eq(TTableFeatureTmp::getWriteType, "是");
        List<TTableFeatureTmp> featureDataList = tTableFeatureTmpService.list(tableQw);
        Map<String, TTableUserConfig> userConfigMap = tTableUserConfigService.list().stream().collect(Collectors.toMap(TTableUserConfig::getPrjctKey, Function.identity(), (v1, v2) -> v1));
        List<RProjectInfo> rProjectInfos = projectInfoService.listAll();
        log.info("[需求管理表写入redmine] project list : {}", rProjectInfos);
        Map<String, RProjectInfo> projectMap = rProjectInfos.stream().collect(Collectors.toMap(e -> String.valueOf(e.getId()), Function.identity(), (v1, v2) -> v1));

        List<AppTableRecord> records = new ArrayList<>();

        // key: pkey, value redmineType
        Map<String, String> redmineTypeMap = rProjectInfos.stream().collect(Collectors.toMap(RProjectInfo::getPkey, RProjectInfo::getRedmineType));

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

            boolean ftrType = "非功能".equals(featureType);

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
            if (ftrType) {
                issue.setStatusId(1).setCreatedOn(new Date());
                issue.setTracker(RedmineConfig.DEV_TRACKER);
                if (!createSubIssue(issue, transport, customFieldList, true, recordsId)) {
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
                } else if (!createSubIssue(parentIssue, transport, customFieldList, false, recordsId)) {
                    featureTmp.setWriteRedmine("3");
                }
            }

            if ("1".equals(featureTmp.getWriteRedmine())) {
                records.add(AppTableRecord.newBuilder().recordId(recordsId).fields(Map.of("需求ID", recordsId)).build());
            }
        }
        List<TTableFeatureTmp> featureTmps = featureDataList.stream().filter(e -> "1".equals(e.getWriteRedmine())).toList();
        if (!CollectionUtils.isEmpty(featureTmps)) {
            tTableFeatureTmpService.updateBatchById(featureTmps);
            log.info("[需求管理表写入redmine] update : {}", featureTmps);
        }

        LambdaQueryWrapper<TTableInfo> lq = Wrappers.lambdaQuery();
        lq.eq(TTableInfo::getTableType, TableTypeContants.FEATURE);
        TTableInfo tTableInfo = tTableInfoService.getOne(lq);
        if (!CollectionUtils.isEmpty(records)) {
            BaseFeishu.table().batchUpdateTableRecords(tTableInfo, records.toArray(new AppTableRecord[0]));
            log.info("[需求管理表写入redmine] 更新成功");
        }

        if (DateUtil.dayOfWeek(new Date()) == 1) {
            LambdaQueryWrapper<TTableFeatureTmp> query = Wrappers.lambdaQuery();
            query.eq(TTableFeatureTmp::getWriteRedmine, "1");
            List<TTableFeatureTmp> tempList = tTableFeatureTmpService.list(query);
            tTableFeatureTmpService.removeBatchByIds(tempList);
            log.info("[需求管理表写入redmine] 周一删除历史数据完成");
        }
        return new ProcessResult(true);
    }

    private static final Map<String, String> ROLE_MAP = Map.of(
            "test", "测试",
            "front", "前端",
            "back", "后端",
            "bgdt", "大数据",
            "prdct", "产品",
            "andrd", "安卓",
            "algrthm", "算法",
            "oprton", "运维",
            "archtct", "架构",
            "implmntton", "实施"
    );
}
