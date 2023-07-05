package com.q.reminder.reminder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.mapper.ProjectInfoMapping;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.vo.*;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.MemberInfoServiceImpl
 * @Description :
 * @date :  2022.09.27 13:24
 */
@Service
public class ProjectInfoServiceImpl extends ServiceImpl<ProjectInfoMapping, RProjectInfo> implements ProjectInfoService {

    @Autowired
    private RdTimeEntryService rdTimeEntryService;
    @Override
    public List<WeeklyProjectVo> getWeeklyDocxList(int weekNumber, String id) {
        return baseMapper.getWeeklyDocxList(weekNumber, id);
    }

    @Override
    public List<WeeklyByProjectVo> weeklyByProjectList(String id, String name) {
        return baseMapper.weeklyByProjectList(id, name);
    }

    @Override
    public List<List<ProjectInfoVo>> listToArray(List<RProjectInfo> list, Map<String, String> userMap, Map<String, String> groupMap, Map<String, Double> projectMap, ProjectParamsVo param) {
        List<List<ProjectInfoVo>> resDate = new ArrayList<>();
        List<String> removeColumn = List.of("createTime", "isDelete");

        // 加班
        List<OvertimeVo> li = rdTimeEntryService.listOvertime(param);
        Map<String, List<OvertimeVo>> userOvertimeMap = li.stream().collect(Collectors.groupingBy(OvertimeVo::getUserId));
        userOvertimeMap.forEach((userId, uList) -> {
            // 取出小于0的数据，并且根据日期分组求和
            Stream<OvertimeVo> overtimeVoStream = uList.stream().filter(e -> e.getAddWork() < 0);
            Map<Date, Double> workMap = overtimeVoStream.collect(Collectors.groupingBy(OvertimeVo::getDay, Collectors.summingDouble(OvertimeVo::getAddWork)));
            workMap.forEach((day, addWork) -> {
                String projectId = uList.stream().filter(e -> e.getDay().equals(day)).findAny().get().getProjectId();
                addWork = (addWork + 8);
                if (addWork < 0 || StringUtils.isBlank(projectId)) {
                    return;
                }
                OvertimeVo vo = new OvertimeVo();
                vo.setDay(day);
                vo.setProjectId(projectId);
                vo.setUserId(userId);
                vo.setAddWork(addWork);
                uList.add(vo);
            });
            uList.removeIf(e -> e.getAddWork() < 0);
        });

        List<OvertimeVo> values = new ArrayList<>();
        for (List<OvertimeVo> value : userOvertimeMap.values()) {
            values.addAll(value);
        }

        Map<String, Double> overtimeMap = values.stream().collect(Collectors.groupingBy(OvertimeVo::getProjectId, Collectors.summingDouble(OvertimeVo::getAddWork)));

        // 工时合计
        Map<String, Double> timeMap = new HashMap<>();
        LambdaQueryWrapper<RdTimeEntry> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.between(RdTimeEntry::getSpentOn, param.getStartTime(), param.getEndTime());
        for (RdTimeEntry e : rdTimeEntryService.list(queryWrapper)) {
            timeMap.merge(String.valueOf(e.getProjectId()), (double) e.getHours(), Double::sum);
        }
        list.forEach(info -> {
            List<ProjectInfoVo> res = new ArrayList<>();
            BeanUtil.beanToMap(info).forEach((k, v) -> {
                if (removeColumn.contains(k)) {
                    return;
                }
                ProjectInfoVo vo = new ProjectInfoVo();
                vo.setValue(v);
                extracted(k, vo);
                res.add(vo);
            });
            String pid = info.getPid();
            ProjectInfoVo pm = new ProjectInfoVo();
            pm.setKey("pmName");
            pm.setValue(userMap.get(info.getPmOu()));
            pm.setColumnType("input");
            pm.setShowEdit(1);
            res.add(pm);
            ProjectInfoVo gm = new ProjectInfoVo();
            gm.setKey("groupName");
            gm.setValue(groupMap.get(info.getSendGroupChatId()));
            gm.setColumnType("input");
            pm.setShowEdit(1);
            res.add(gm);

            // 成本
            ProjectInfoVo cost = new ProjectInfoVo();
            cost.setKey("costName");
            Double costDouble = projectMap.get(pid);
            if (costDouble == null) {
                costDouble = 0.0;
            }
            cost.setValue(BigDecimal.valueOf(costDouble).setScale(2, RoundingMode.HALF_UP).doubleValue());
            cost.setColumnType("input");
            cost.setShowEdit(1);
            cost.setLabel("目前成本（万元）");
            res.add(cost);

            // 人力合计（小时）
            ProjectInfoVo people = new ProjectInfoVo();
            people.setKey("peopleTotal");
            Double peopleDouble = timeMap.get(pid);
            if (peopleDouble == null) {
                peopleDouble = 0.0;
            }
            people.setValue(BigDecimal.valueOf(peopleDouble).setScale(2, RoundingMode.HALF_UP).doubleValue());
            people.setColumnType("input");
            people.setShowEdit(1);
            people.setLabel("人力合计（小时）");
            res.add(people);

            // 加班合计（小时）
            ProjectInfoVo overtime = new ProjectInfoVo();
            overtime.setKey("overtimeTotal");
            Double overtimeDouble = overtimeMap.get(pid);
            if (overtimeDouble == null) {
                overtimeDouble = 0.0;
            }
            overtime.setValue(BigDecimal.valueOf(overtimeDouble).setScale(2, RoundingMode.HALF_UP).doubleValue());
            overtime.setColumnType("input");
            overtime.setShowEdit(1);
            overtime.setLabel("加班合计（小时）");
            res.add(overtime);

            // 正常合计（小时）
            ProjectInfoVo work = new ProjectInfoVo();
            work.setKey("workTotal");
            work.setValue(BigDecimal.valueOf(peopleDouble - overtimeDouble).setScale(2, RoundingMode.HALF_UP).doubleValue());
            work.setColumnType("input");
            work.setShowEdit(1);
            work.setLabel("正常合计（小时）");
            res.add(work);

            resDate.add(res);
        });
        return resDate;
    }

    private void extracted(String k, ProjectInfoVo vo) {
        vo.setKey(k);
        vo.setLabel(map().get(k));
        vo.setColumnType("input");
        vo.setShowEdit(0);
        Object radio = radio().get(k);
        if (!Objects.equals(radio, null)) {
            vo.setColumnType("radio");
            vo.setColumnDesc(radio);
        }
        if ("productMemberId".equals(k) || "pmOu".equals(k)) {
            vo.setColumnType("api");
            vo.setColumnDesc(Map.of("url", "/p/member", "method", "GET", "return", "array"));
        }
        if ("sendGroupChatId".equals(k)) {
            vo.setColumnType("api");
            vo.setColumnDesc(Map.of("url", "/p/group", "method", "GET", "return", "array"));
        }
        if ("pname".equals(k)) {
            vo.setColumnType("api");
            vo.setColumnDesc(Map.of("url", "/p/userInfoLit", "method", "GET", "return", "array"));
        }
        if ("startDay".equals(k)) {
            vo.setColumnType("date");
        }
        List<String> enable = List.of("id", "pid", "updateTime", "isDelete", "redmineUrl");
        if (enable.contains(k)) {
            vo.setShowEdit(1);
        }
    }

    @Override
    public List<ProjectInfoVo> listInfo() {
        List<ProjectInfoVo> res = new ArrayList<>();
        map().forEach((k, v) -> {
            ProjectInfoVo vo = new ProjectInfoVo();
            extracted(k, vo);
            res.add(vo);
        });
        return res;
    }

    @Override
    @Cacheable(cacheNames = RedisKeyContents.REDMINE_PROJECT_ALL, key = "'projectAll'")
    public List<RProjectInfo> listAll() {
        return list();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyContents.REDMINE_PROJECT_ALL, key = "#info.getPkey()")
    public void updateInfo(RProjectReaVo info) {
        update(info, Wrappers.<RProjectInfo>lambdaUpdate().eq(RProjectInfo::getPkey, info.getPkey()));
    }

    @Override
    @Cacheable(cacheNames = RedisKeyContents.REDMINE_PROJECT_KEY, key = "#prjctKey", unless = "#prjctKey == null")
    public RProjectInfo projectInfoByPrjctKey(String prjctKey) {
        LambdaQueryWrapper<RProjectInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(RProjectInfo::getPkey, prjctKey);
        return getOne(lambdaQueryWrapper);
    }

    @Override
    public Map<String, Double> getProjectCost() {
        List<Map<String, Double>> map = baseMapper.getProjectCost();
        return map.stream().collect(Collectors.toMap(e -> String.valueOf(e.get("projectId")), f -> Double.valueOf(String.valueOf(f.get("cost"))), (v1, v2) -> v1));
    }


    private Map<String, Object> radio() {
        List<Map<String, String>> radios = List.of(Map.of("0", "是"), Map.of("1", "否"));
        return Map.of(
//                "syncFeature", radios,
                "isSendGroup", radios,
                "weeklyCopyType", radios,
                "weeklyType", radios,
                "wikiType", radios,
                "redmineType", List.of(Map.of("1", "旧"), Map.of("2", "新")),
                "isDelete", List.of(Map.of("1", "是"), Map.of("0", "否"))
        );
    }

    private Map<String, String> map() {
        Map<String, String> resMap = new LinkedHashMap<>();
//        resMap.put("pid", "redmine项目ID");
        resMap.put("pkey", "redmine项目Key");
        resMap.put("pname", "redmine项目名称");
        resMap.put("budget", "项目预算（万元）");
//        resMap.put("featureToken", "需求管理表Token");
//        resMap.put("syncFeature", "同步需求管理表");
        resMap.put("projectShortName", "项目短名称");
        resMap.put("pmOu", "项目经理");
        resMap.put("pmKey", "项目经理RedmineKey");
//        resMap.put("redmineUrl", "RedmineURL");
        resMap.put("startDay", "项目开始时间");
        resMap.put("sendGroupChatId", "项目需求群");
        resMap.put("isSendGroup", "发送需求群");
        resMap.put("productMemberId", "产品经理");
        resMap.put("folderToken", "项目周报文件夹Token");
        resMap.put("weeklyCopyType", "复制项目周报");
        resMap.put("weeklyType", "自动生成项目周报");
        resMap.put("wikiType", "自动复制标准过程表");
        resMap.put("wikiToken", "知识库Token");
        resMap.put("redmineType", "Redmine类型");
        resMap.put("isDelete", "删除标识");
        return resMap;
    }
}
