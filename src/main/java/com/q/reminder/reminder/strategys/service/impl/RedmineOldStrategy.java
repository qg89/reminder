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
@RedmineTypeAnnotation(type = ReminderTypeEnum.OLD)
public class RedmineOldStrategy extends AbstractRedmineStrategy {

    @Override
    public List<CustomField> getCustomField(String featureValue) {
        List<CustomField> customFields = new ArrayList<>();
        CustomField featureType = new CustomField()
                .setId(42)
                .setName("需求类型")
                .setValue("功能");
        CustomField featureVel = new CustomField()
                .setId(30)
                .setName("是否需要验证")
                .setValue("是");
        CustomField featureId = new CustomField()
                .setId(5)
                .setName("需求ID")
                .setValue(featureValue);
        customFields.add(featureType);
        customFields.add(featureVel);
        customFields.add(featureId);
        return customFields;
    }

    @Override
    public Tracker getDevTracker() {
        return new Tracker().setId(7).setName("开发");
    }

    @Override
    public Tracker getTestTracker() {
        return new Tracker().setId(8).setName("测试");
    }

    @Override
    public List<RequestParam> getFeatureIdParams(String value) {
        return List.of(new RequestParam("f[]", "cf_5"),
                new RequestParam("op[cf_5]", "~"),
                new RequestParam("v[cf_5][]", value));
    }


    /**
     * 1:New
     * 2:In Progress
     * 3:Resolved
     * 5:Closed
     * 6:Rejected
     * 7:重新激活
     * 12:Released
     * 11:Reviewed
     * 13:Defer
     * @return
     */
    @Override
    public Set<String> getIssueStatusIds() {
        return Set.of("5");
    }
}
