package com.q.reminder.reminder.task.table;

import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.q.reminder.reminder.constant.TableTypeContants;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.service.TTableInfoService;
import com.q.reminder.reminder.util.FeishuJavaUtils;
import com.q.reminder.reminder.vo.table.FeatureVo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncTableRecordSampleTask
 * @Description : 同步多维度表格记录-需求管理表写入redmine
 * @date :  2023.01.17 11:40
 */
@Log4j2
@Component
public class SyncTableRecordsWriteRedmineTask {
    @Autowired
    private Client client;
    @Autowired
    private TTableInfoService tTableInfoService;

    @XxlJob("syncTableRecordTask")
    public void syncTableRecordTask() throws Exception {
        List<FeatureVo> list = tTableInfoService.listByTableType(TableTypeContants.FEATURE_TMP);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        FeatureVo featureVo = list.get(0);
        if (featureVo == null) {
            return;
        }
        List<String> columns = list.stream().map(FeatureVo::getColumnName).toList();
        TTableInfo vo = new TTableInfo();
        vo.setAppToken(featureVo.getAppToken());
        vo.setTableId(featureVo.getTableId());
        vo.setViewId(featureVo.getViewId());
        List<AppTableRecord> appTableRecords = FeishuJavaUtils.listTableRecords(client, vo);
        for (AppTableRecord tableRecord : appTableRecords) {
            Map<String, Object> fields = tableRecord.getFields();

        }
    }
}
