package com.q.reminder.reminder.task.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
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
import com.q.reminder.reminder.vo.MessageVo;
import com.q.reminder.reminder.vo.RedmineDataVo;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
@RequiredArgsConstructor
public class SyncFeatureDatasWriteRedmineTask implements BasicProcessor {
    private final TTableFeatureTmpService tTableFeatureTmpService;
    private final TTableInfoService tTableInfoService;

    private Date dueDate = DateTime.now().plusDays(7).toDate();

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult processResult = new ProcessResult(true);
        StringBuffer sendAdmin = new StringBuffer();
        try {
            List<RedmineDataVo> featureDataList = tTableFeatureTmpService.listByProject();
            Map<String, List<FeautreTimeVo>> featureTimeMap = tTableFeatureTmpService.queryAllTimes().stream().collect(Collectors.groupingBy(FeautreTimeVo::getRecordsId));
            Tracker featureTracker = new Tracker().setId(2).setName("需求");
            List<AppTableRecord> records = new ArrayList<>();
            List<TTableFeatureTmp> featureTmps = new ArrayList<>();
            featureDataList.stream().collect(Collectors.groupingBy(RedmineDataVo::getRedmineType)).forEach((type, list) -> {
                log.info("[需求管理表写入redmine] type : {}", type);
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
                    if (CollectionUtils.isEmpty(feautreTimeVos)) {
                        log.error("[需求管理表写入redmine] 获取记录为空");
                        sendAdmin.append("[需求管理表写入redmine] 获取记录为空!\r\n recordId: ").append(recordsId).append(", type : ").append(type).append("  \r\n");
                        continue;
                    }

                    boolean ftrType = "非功能".equals(featureType);

                    RProjectInfo project = new RProjectInfo();
                    project.setRedmineUrl(redmineDataVo.getRedmineUrl());
                    project.setPkey(redmineDataVo.getPrjctKey());
                    project.setPmKey(redmineDataVo.getPmKey());
                    Transport transport = RedmineApi.getTransportByProject(project);
                    if (RedmineApi.checkIssue(transport, requestParams)) {
                        log.info("[需求管理表写入redmine] 已存在，recordsId {}", recordsId);
                        redmineDataVo.setWriteRedmine("4");
                        tTableFeatureTmpService.updateById(redmineDataVo);
                        continue;
                    }
                    String subject = RedmineApi.createSubject(redmineDataVo);
                    if (StringUtils.isBlank(subject)) {
                        subject = "默认主题";
                    }
                    Issue issue = new Issue();
                    issue.setSubject(subject);
                    issue.setDescription(dscrptn);
                    issue.setAssigneeId(redmineDataVo.getPrdctId());
                    issue.setDueDate(dueDate);
                    if (prodTime != null) {
                        dueDate = Date.from(prodTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                        issue.setDueDate(dueDate);
                        log.info("[需求管理表写入redmine] 生产发布时间:{}", prodTime);
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
                        log.info("[需求管理表写入redmine] 创建子任务结束, {} 功能需求", ftrType);
                    } else {
                        issue.setTracker(featureTracker);
                        try {
                            parentIssue = RedmineApi.createIssue(issue, transport);
                        } catch (RedmineException e) {
                            processResult.setMsg("创建子任务异常");
                            processResult.setSuccess(false);
                            log.error("[多维表格-创建redmine任务]父任务异常", e);
                        }
                        if (parentIssue.getId() == null) {
                            redmineDataVo.setWriteRedmine("2");
                            log.info("[需求管理表写入redmine] 创建父任务失败, {} 功能需求", ftrType);
                        } else {
                            if (!RedmineApi.createSubIssue(parentIssue, transport, customFieldList, false, feautreTimeVos, testTracker, devTracker)) {
                                redmineDataVo.setWriteRedmine("3");
                                log.info("[需求管理表写入redmine] 创建子任务失败, {} 功能需求", ftrType);
                            }
                        }
                        log.info("[需求管理表写入redmine] 创建子任务结束, {} 功能需求", ftrType);
                    }

                    if ("1".equals(redmineDataVo.getWriteRedmine())) {
                        records.add(AppTableRecord.newBuilder().recordId(recordsId).fields(Map.of("需求ID", recordsId)).build());
                    }
                    featureTmps.add(redmineDataVo);
                }
            });
            if (!sendAdmin.isEmpty()) {
                MessageVo vo = new MessageVo();
                vo.setContent(sendAdmin.toString());
                vo.setMsgType("post");
                vo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
                vo.setReceiveId("ou_35e03d4d8754dd35fed26c26849c85ab");
                BaseFeishu.message().sendContent(vo);
            }
            if (!CollectionUtils.isEmpty(featureTmps)) {
                tTableFeatureTmpService.updateBatchById(featureTmps);
                log.info("[需求管理表写入redmine] update size: {}", featureTmps.size());
            }

            LambdaQueryWrapper<TTableInfo> lq = Wrappers.lambdaQuery();
            lq.eq(TTableInfo::getTableType, TableTypeContants.FEATURE);
            TTableInfo tTableInfo = tTableInfoService.getOne(lq);
            if (!CollectionUtils.isEmpty(records)) {
                BaseFeishu.cloud().table().batchUpdateTableRecords(tTableInfo, records.toArray(new AppTableRecord[0]));
                log.info("[需求管理表写入redmine] 更新多维表格 完成， size：{}", records.size());
            }

            log.info("[需求管理表写入redmine] 执行完成");
        } catch (Exception e) {
            processResult.setMsg("[需求管理表写入redmine] 执行异常");
            processResult.setSuccess(false);
            log.error("[需求管理表写入redmine] 执行异常", e);
        }
        tTableFeatureTmpService.removeBatchByIds(tTableFeatureTmpService.list(Wrappers.<TTableFeatureTmp>lambdaQuery().in(TTableFeatureTmp::getWriteRedmine, "1", "4")));
        log.info("[需求管理表写入redmine] 删除历史数据完成!");
        return processResult;
    }
}
