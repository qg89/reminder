package com.q.reminder.reminder.service.otherService;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.constant.TableTypeContants;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.service.TTableInfoService;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.FeatureAllVo;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.otherService.TableFeatureService
 * @Description :
 * @date :  2023/7/25 16:52
 */
@Service
@AllArgsConstructor
public class TableFeatureService {
    public final TTableInfoService tTableInfoService;
    public final RedisTemplate<String, Object> redisTemplate;

    public List<FeatureAllVo> records(String projectName) {
        Object recordsObj = redisTemplate.opsForValue().get(RedisKeyContents.TABLE_RECORDS);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(RedisKeyContents.TABLE_RECORDS)) && recordsObj != null) {
            return (List<FeatureAllVo>) recordsObj;
        }
        List<FeatureAllVo> featureAllVos = new ArrayList<>();
        TTableInfo vo = tTableInfoService.getOne(Wrappers.<TTableInfo>lambdaQuery().eq(TTableInfo::getTableType, TableTypeContants.FEATURE));
        List<AppTableRecord> appTableRecords = BaseFeishu.cloud().table().listTableRecords(vo);
        List<AppTableRecord> tableRecords = appTableRecords.stream().filter(e ->
                e.getFields().containsValue(projectName)
        ).toList();
        for (AppTableRecord record : tableRecords) {
            FeatureAllVo op = new FeatureAllVo();
            op.setValue(record.getRecordId());
            StringBuilder value = new StringBuilder();
            record.getFields().forEach((k, v) -> {
                switch (k) {
                    case "模块", "一级", "二级" -> value.append(v).append("，");
                    default -> {
                    }
                }
            });
            op.setLabel(value.deleteCharAt(value.length() - 1).toString());
            featureAllVos.add(op);
        }
        if (!CollectionUtils.isEmpty(featureAllVos)) {
            redisTemplate.opsForValue().set(RedisKeyContents.TABLE_RECORDS, featureAllVos, 1, TimeUnit.HOURS);
        }
        return featureAllVos;
    }
}
