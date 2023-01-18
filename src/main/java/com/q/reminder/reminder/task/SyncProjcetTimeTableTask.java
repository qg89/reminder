package com.q.reminder.reminder.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.q.reminder.reminder.constant.TableTypeContants;
import com.q.reminder.reminder.entity.TTableColumn;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.service.TTableColumnService;
import com.q.reminder.reminder.service.TTableInfoService;
import com.q.reminder.reminder.util.FeishuJavaUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncProjcetTimeTableTask
 * @Description : 同步工时
 * @date :  2023.01.18 11:28
 */
@Component
@Log4j2
public class SyncProjcetTimeTableTask {
    @Autowired
    private Client client;
    @Autowired
    private TTableInfoService tTableInfoService;
    @Autowired
    private TTableColumnService tTableColumnService;

    @XxlJob("syncProjcetTimeTableTask")
    public void syncProjcetTimeTableTask() throws Exception {
        LambdaQueryWrapper<TTableInfo> qw = Wrappers.lambdaQuery();
        qw.eq(TTableInfo::getTableType, TableTypeContants.PROJECT_TIME);
        Map<String, TTableInfo> tableMap = tTableInfoService.list(qw).stream().collect(Collectors.toMap(TTableInfo::getViewId, Function.identity(), (v1, v2) -> v1));
        // 获取全部列
        TTableInfo tableInfo = tableMap.get(TableTypeContants.ViewType.ALL);
        LambdaQueryWrapper<TTableColumn> columnQW = Wrappers.lambdaQuery();
        columnQW.eq(TTableColumn::getTableId, tableInfo.getId());
        columnQW.eq(TTableColumn::getEnable, "0");
        columnQW.select(TTableColumn::getColumnName);
        Map<String, String> map = tTableColumnService.list(columnQW).stream().collect(Collectors.toMap(TTableColumn::getColumnName, e -> ""));

        // 昨天的数据
        List<String> list = FeishuJavaUtils.listTableRecords(client, tableMap.get(TableTypeContants.ViewType.YESTDAY)).stream().map(AppTableRecord::getRecordId).toList();
        FeishuJavaUtils.batchDeleteTableRecords(client,tableMap.get(TableTypeContants.ViewType.YESTDAY), list.toArray(new String[list.size()]));

    }
}
