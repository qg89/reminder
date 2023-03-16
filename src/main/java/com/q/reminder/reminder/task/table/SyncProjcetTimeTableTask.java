package com.q.reminder.reminder.task.table;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.q.reminder.reminder.constant.TableTypeContants;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.entity.TTableUserTime;
import com.q.reminder.reminder.service.TTableInfoService;
import com.q.reminder.reminder.service.TTableUserTimeService;
import com.q.reminder.reminder.service.WUserTimesService;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncProjcetTimeTableTask
 * @Description : 同步工时到多维表格-将数据库中的数据同步到多维表格中
 * @date :  2023.01.18 11:28
 */
@Component
public class SyncProjcetTimeTableTask implements BasicProcessor {
    @Autowired
    private TTableInfoService tTableInfoService;
    @Autowired
    private WUserTimesService wUserTimesService;
    @Autowired
    private TTableUserTimeService tTableUserTimeService;

    //    @XxlJob("syncProjcetTimeTableTask")
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger log = context.getOmsLogger();
        String jobParam = context.getJobParams();
        String day = "";

        LambdaQueryWrapper<TTableInfo> qw = Wrappers.lambdaQuery();
        qw.eq(TTableInfo::getTableType, TableTypeContants.PROJECT_TIME);
        Map<String, TTableInfo> tableMap = tTableInfoService.list(qw).stream().collect(Collectors.toMap(TTableInfo::getViewType, Function.identity(), (v1, v2) -> v1));
        // 获取全部列
        TTableInfo tableInfo = tableMap.get(TableTypeContants.ViewType.ALL);
        Integer tableInfoId = tableInfo.getId();

        DateTime yesterday = DateUtil.yesterday();
        if (Objects.equals(jobParam, TableTypeContants.ViewType.YESTDAY)) {
            day = yesterday.toString("YYYY-MM-dd");
            delRecords(tableMap.get(TableTypeContants.ViewType.YESTDAY));
        }
        if (Objects.equals(jobParam, TableTypeContants.ViewType.LAST_MONTH)) {
            day = DateUtil.lastMonth().toString("YYYY-MM");
            delRecords(tableMap.get(TableTypeContants.ViewType.LAST_MONTH));
        }
        if (Objects.equals(jobParam, TableTypeContants.ViewType.THIS_MONTH)) {
            day = yesterday.toString("YYYY-MM");
            delRecords(tableMap.get(TableTypeContants.ViewType.THIS_MONTH));
        }
        List<Map<String, Object>> userTimeMap = wUserTimesService.listByTable(day, jobParam);

        LambdaQueryWrapper<TTableUserTime> tableUserQW = Wrappers.lambdaQuery();
        tableUserQW.eq(TTableUserTime::getTableId, tableInfoId);
        Map<String, String> columnMap = tTableUserTimeService.list(tableUserQW).stream().collect(Collectors.toMap(TTableUserTime::getColumnEntity, TTableUserTime::getTableColumnName));
        List<AppTableRecord> records = new ArrayList<>();
        for (Map<String, Object> m : userTimeMap) {
            Map<String, Object> data = new HashMap<>();
            m.forEach((k, v) -> {
                data.put(columnMap.get(k), v);
            });
            records.add(AppTableRecord.newBuilder().fields(data).build());
        }
        BaseFeishu.table().batchCreateTableRecords(tableInfo, records.toArray(new AppTableRecord[0]));
        return new ProcessResult(true);
    }

    /**
     * 删除记录数据
     *
     * @param tTableInfo
     * @throws Exception
     */
    private void delRecords(TTableInfo tTableInfo) throws Exception {
        List<String> list = BaseFeishu.table().listTableRecords(tTableInfo).stream().map(AppTableRecord::getRecordId).toList();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        BaseFeishu.table().batchDeleteTableRecords(tTableInfo, list.toArray(new String[0]));
    }
}
