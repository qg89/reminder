package com.q.reminder.reminder.task.table;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.q.reminder.reminder.constant.TableTypeContants;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.service.TTableFeatureTmpService;
import com.q.reminder.reminder.service.TTableInfoService;
import com.q.reminder.reminder.strategys.config.HandlerTypeContext;
import com.q.reminder.reminder.strategys.service.RedmineTypeStrategy;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.FeautreTimeVo;
import com.q.reminder.reminder.vo.RedmineDataVo;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;
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

    private Date dueDate = DateTime.now().plusDays(7).toDate();

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger log = context.getOmsLogger();
        ProcessResult processResult = new ProcessResult(true);
        List<RedmineDataVo> featureDataList = tTableFeatureTmpService.listByProject();
        Map<String, List<FeautreTimeVo>> featureTimeMap = tTableFeatureTmpService.queryAllTimes().stream().collect(Collectors.groupingBy(FeautreTimeVo::getRecordsId));
        Tracker featureTracker = new Tracker().setId(2).setName("需求");
        List<AppTableRecord> records = new ArrayList<>();
        List<TTableFeatureTmp> featureTmps = new ArrayList<>();
        featureDataList.stream().collect(Collectors.groupingBy(RedmineDataVo::getRedmineType)).forEach((type, list) -> {
            RedmineTypeStrategy redmineTypeStrategy = HandlerTypeContext.getInstance(Integer.parseInt(type));
            Tracker devTracker = redmineTypeStrategy.getDevTracker();
            Tracker testTracker = redmineTypeStrategy.getTestTracker();
            for (RedmineDataVo redmineDataVo : list) {
                String recordsId = redmineDataVo.getRecordsId();
                List<CustomField> customFieldList = redmineTypeStrategy.getCustomField(recordsId);
                List<RequestParam> requestParams = redmineTypeStrategy.getFeatureIdParams(recordsId);
                String dscrptn = redmineDataVo.getDscrptn();
                Float prdct = redmineDataVo.getPrdct();
                LocalDate prodTime = redmineDataVo.getProdTime();
                String featureType = redmineDataVo.getFeatureType();
                List<FeautreTimeVo> feautreTimeVos = featureTimeMap.get(recordsId);

                boolean ftrType = "非功能".equals(featureType);

                RProjectInfo project = new RProjectInfo();
                project.setRedmineUrl(redmineDataVo.getRedmineUrl());
                project.setPkey(redmineDataVo.getPrjctKey());
                project.setPmKey(redmineDataVo.getPmKey());
                Transport transport = RedmineApi.getTransportByProject(project);
                if (RedmineApi.checkIssue(transport, requestParams)) {
                    continue;
                }
                String subject = RedmineApi.createSubject(redmineDataVo);
                Issue issue = new Issue();
                issue.setSubject(subject);
                issue.setDescription(dscrptn);
                issue.setAssigneeId(redmineDataVo.getPrdctId());
                issue.setDueDate(dueDate);
                if (prodTime != null) {
                    dueDate = Date.from(prodTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    issue.setDueDate(dueDate);
                }
                issue.setSpentHours(prdct);
                issue.setProjectId(redmineDataVo.getPId());
                issue.addCustomFields(customFieldList);
                Issue parentIssue = new Issue();
                redmineDataVo.setWriteRedmine("1");
                if (ftrType) {
                    issue.setStatusId(1).setCreatedOn(new Date());
                    issue.setTracker(devTracker);
                    if (!RedmineApi.createSubIssue(issue, transport, customFieldList, true, feautreTimeVos, testTracker, devTracker)) {
                        redmineDataVo.setWriteRedmine("3");
                    }
                } else {
                    issue.setTracker(featureTracker);
                    try {
                        parentIssue = RedmineApi.createIssue(issue, transport);
                    } catch (RedmineException e) {
                        log.error("[多维表格-创建redmine任务]父任务异常", e);
                        processResult.setMsg("创建子任务异常");
                        processResult.setSuccess(false);
                    }
                    if (parentIssue.getId() == null) {
                        redmineDataVo.setWriteRedmine("2");
                    } else {
                        if (!RedmineApi.createSubIssue(parentIssue, transport, customFieldList, false, feautreTimeVos, testTracker, devTracker)) {
                            redmineDataVo.setWriteRedmine("3");
                        }
                    }
                }

                if ("1".equals(redmineDataVo.getWriteRedmine())) {
                    records.add(AppTableRecord.newBuilder().recordId(recordsId).fields(Map.of("需求ID", recordsId)).build());
                }
                featureTmps.add(redmineDataVo);
            }
        });
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
        log.info("[需求管理表写入redmine] 执行完成");
        return processResult;
    }
}
