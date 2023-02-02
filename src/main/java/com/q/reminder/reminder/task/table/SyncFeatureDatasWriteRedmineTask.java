package com.q.reminder.reminder.task.table;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.Client;
import com.q.reminder.reminder.constant.TableTypeContants;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.service.TTableFeatureTmpService;
import com.q.reminder.reminder.service.TTableInfoService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

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
    private TTableInfoService tTableInfoService;
    @Autowired
    private TTableFeatureTmpService tTableFeatureTmpService;

    @XxlJob("syncTableRecordTask")
    public void syncTableRecordTask() throws Exception {
        List<TTableFeatureTmp> featureDataList = tTableFeatureTmpService.list();
        LambdaQueryWrapper<TTableInfo> qw = Wrappers.lambdaQuery();
        qw.eq(TTableInfo::getTableType, TableTypeContants.FEATURE);
        List<TTableInfo> list = tTableInfoService.list(qw);
        if (DateUtil.dayOfWeek(new Date()) == 1) {
            LambdaQueryWrapper<TTableFeatureTmp> query = Wrappers.lambdaQuery();
            query.eq(TTableFeatureTmp::getWriteRedmine, "1");
            List<TTableFeatureTmp> tempList = tTableFeatureTmpService.list(query);
            tTableFeatureTmpService.removeBatchByIds(tempList);
        }
    }
}
