package com.q.reminder.reminder.strategys.service;

import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.RedmineStrategy
 * @Description :
 * @date :  2023.03.27 10:36
 */
public interface RedmineTypeStrategy {

    List<CustomField> getCustomField(String featureValue);

    Tracker getDevTracker();
    Tracker getTestTracker();

    List<RequestParam> getFeatureIdParams(String value);

}
