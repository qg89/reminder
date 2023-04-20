package com.q.reminder.reminder.strategys.service.impl;

import com.q.reminder.reminder.strategys.service.RedmineTypeStrategy;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.OldRemineStrategy
 * @Description :
 * @date :  2023.03.27 10:37
 */
public abstract class AbstractRedmineStrategy implements RedmineTypeStrategy {

    @Override
    public List<CustomField> getCustomField(String featureValue) {
        return new ArrayList<>();
    }

    @Override
    public Tracker getDevTracker(){
        return new Tracker();
    }
    @Override
    public Tracker getTestTracker(){
        return new Tracker();
    }
    @Override
    public List<RequestParam> getFeatureIdParams(String value){
        return new ArrayList<>();
    }

    public Set<String> getIssueStatusIds() {
        return new HashSet<>();
    }
}
