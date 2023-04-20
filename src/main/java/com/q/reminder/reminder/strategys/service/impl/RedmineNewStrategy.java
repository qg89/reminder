package com.q.reminder.reminder.strategys.service.impl;

import com.q.reminder.reminder.enums.ReminderTypeEnum;
import com.q.reminder.reminder.strategys.anno.RedmineTypeAnnotation;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.RedmineOldStrategy
 * @Description :
 * @date :  2023.03.27 10:40
 */
@Log4j2
@Component
@RedmineTypeAnnotation(type = ReminderTypeEnum.NEW)
public class RedmineNewStrategy extends AbstractRedmineStrategy {

    @Override
    public List<CustomField> getCustomField(String featureValue) {
        List<CustomField> customFieldList = new ArrayList<>();
        CustomField featureCustom = new CustomField()
                .setId(226)
                .setName("需求ID")
                .setValue(featureValue);
        CustomField featureType = new CustomField()
                .setId(286)
                .setName("需求类型")
                .setValue("功能");
        customFieldList.add(featureType);
        customFieldList.add(featureCustom);
        return customFieldList;
    }

    @Override
    public Tracker getDevTracker() {
        return new Tracker().setId(3).setName("研发");
    }

    @Override
    public Tracker getTestTracker() {
        return new Tracker().setId(4).setName("测试");
    }

    @Override
    public List<RequestParam> getFeatureIdParams(String value) {
        return List.of(new RequestParam("f[]", "cf_226"),
                new RequestParam("op[cf_226]", "~"),
                new RequestParam("v[cf_226][]", value));
    }

    /**
     * 4:已关闭
     * 3：已解决
     * 1：新
     * 2：处理中
     * 5：已拒绝
     * 6：专车是
     * 7：转产品
     * 8：重新打开
     * @return
     */
    @Override
    public Set<String> getIssueStatusIds() {
        return Set.of("4");
    }

}
