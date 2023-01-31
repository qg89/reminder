package com.q.reminder.reminder.task.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.model.AppTableField;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncTableColumnTask
 * @Description : 同步多维表格各个表中列到数据库
 * @date :  2023.01.18 14:11
 */
@Component
@Log4j2
public class SyncTableColumnTask {
    @Autowired
    private Client client;
    @Autowired
    private TTableInfoService tTableInfoService;
    @Autowired
    private TTableColumnService tTableColumnService;


    @XxlJob("syncTableColumn")
    public void syncTableColumn() throws Exception {
        LambdaQueryWrapper<TTableInfo> qw = Wrappers.lambdaQuery();
        qw.eq(TTableInfo::getViewType, TableTypeContants.ViewType.ALL);
        List<TTableColumn> data = new ArrayList<>();
        for (TTableInfo vo : tTableInfoService.list(qw)) {
            Integer id = vo.getId();
            List<AppTableField> appTableFields = FeishuJavaUtils.listTableColumn(client, vo);
            appTableFields.forEach(e -> {
                TTableColumn column = new TTableColumn();
                column.setColumnName(e.getFieldName());
                column.setTableId(id);
                data.add(column);
            });
        }
        tTableColumnService.saveOrUpdateBatchByMultiId(data);
    }
}
