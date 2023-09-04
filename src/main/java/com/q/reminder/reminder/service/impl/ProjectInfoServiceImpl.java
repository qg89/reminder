package com.q.reminder.reminder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.mapper.ProjectInfoMapping;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.service.otherService.COPQByDayService;
import com.q.reminder.reminder.vo.*;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import com.taskadapter.redmineapi.RedmineException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.log.impl.OmsNullLogger;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private COPQByDayService copqByDayService;


    @Override
    public List<WeeklyProjectVo> getWeeklyDocxList(int weekNumber, String id) {
        return baseMapper.getWeeklyDocxList(weekNumber, id);
    }

    @Override
    public List<WeeklyByProjectVo> weeklyByProjectList(String id, String name) {
        return baseMapper.weeklyByProjectList(id, name);
    }

    @Override
    public List<List<ProjectInfoVo<?>>> listToArray(List<RProjectInfo> list, Map<String, String> userMap, Map<String, String> groupMap, Map<String, Double> projectMap, ProjectParamsVo param) {
        List<List<ProjectInfoVo<?>>> resDate = new ArrayList<>();
        List<String> removeColumn = List.of("createTime", "isDelete");

        Map<String, String> copqMap = new HashMap<>();
        copqMap = getProjectCopqMap();

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
        for (RdTimeEntry e : rdTimeEntryService.listByProject(param)) {
            timeMap.merge(String.valueOf(e.getProjectId()), (double) e.getHours(), Double::sum);
        }
        Map<String, String> finalCopqMap = copqMap;
        list.forEach(info -> {
            List<ProjectInfoVo<?>> res = new ArrayList<>();
            BeanUtil.beanToMap(info).forEach((k, v) -> {
                if (removeColumn.contains(k)) {
                    return;
                }
                ProjectInfoVo<Object> vo = new ProjectInfoVo<>();
                vo.setValue(v);
                extracted(k, vo);
                res.add(vo);
            });
            String pid = info.getPid();
            ProjectInfoVo<String> pm = new ProjectInfoVo<>();
            pm.setKey("pmName");
            pm.setValue(userMap.get(info.getPmOu()));
            pm.setColumnType("input");
            pm.setShowEdit(1);
            res.add(pm);
            ProjectInfoVo<String> gm = new ProjectInfoVo<>();
            gm.setKey("groupName");
            gm.setValue(groupMap.get(info.getSendGroupChatId()));
            gm.setColumnType("input");
            pm.setShowEdit(1);
            res.add(gm);

            // 成本
            ProjectInfoVo<Double> cost = new ProjectInfoVo<>();
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
            ProjectInfoVo<Double> people = new ProjectInfoVo<>();
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

            ProjectInfoVo<Double> peopleMonth = new ProjectInfoVo<>();
            peopleMonth.setValue(BigDecimal.valueOf(peopleDouble / 8 / 21.75).setScale(2, RoundingMode.HALF_UP).doubleValue());
            peopleMonth.setLabel("人力合计（人月）");
            peopleMonth.setKey("peopleMonth");
            peopleMonth.setShowEdit(1);
            res.add(peopleMonth);

            // 加班合计（小时）
            ProjectInfoVo<Double> overtime = new ProjectInfoVo<>();
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
            ProjectInfoVo<Double> work = new ProjectInfoVo<>();
            work.setKey("workTotal");
            work.setValue(BigDecimal.valueOf(peopleDouble - overtimeDouble).setScale(2, RoundingMode.HALF_UP).doubleValue());
            work.setColumnType("input");
            work.setShowEdit(1);
            work.setLabel("正常合计（小时）");
            res.add(work);

            // 正常合计（小时）
            ProjectInfoVo<String> copq = new ProjectInfoVo<>();
            copq.setKey("copq");
            copq.setValue(finalCopqMap.get(pid));
            copq.setColumnType("input");
            copq.setShowEdit(1);
            copq.setLabel("COPQ（Cost Of Poor Quality）");
            res.add(copq);

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
    @Cacheable(cacheNames = RedisKeyContents.REDMINE_PROJECT_ALL, key = "'projectAll'", unless = "#result == null")
    public List<RProjectInfo> listAll() {
        return list();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyContents.REDMINE_PROJECT_ALL, key = "#info.getPkey()")
    public void updateInfo(RProjectReaVo info) {
        update(info, Wrappers.<RProjectInfo>lambdaUpdate().eq(RProjectInfo::getPkey, info.getPkey()));
    }

    @Override
    @Cacheable(cacheNames = RedisKeyContents.REDMINE_PROJECT_KEY, key = "#prjctKey", unless = "#prjctKey == null and #result == null")
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

    @Override
    public List<ProjectCostVo> projectCost(ProjectParamsVo param) throws RedmineException {
        List<RProjectInfo> projectInfoList = this.listAll();
        List<ProjectCostVo> list = new ArrayList<>();
        // 处理成本
        Map<String, Double> projectMap = getProjectCost();
        List<ProjectCostVo> costList = rdTimeEntryService.listBySpentOnToCost(param);
        // 处理合计人月
        Map<String, Double> peopleMonthsMap = costList.stream().collect(Collectors.groupingBy(ProjectCostVo::getPid, Collectors.summingDouble(ProjectCostVo::getPeopleMonth)));
        // 处理加班合计
        Map<String, Double> overtimeMap = getProjectOvertimeList(costList);
        Map<String, Double> peopleHoursMap = costList.stream().collect(Collectors.groupingBy(ProjectCostVo::getPid, Collectors.summingDouble(ProjectCostVo::getPeopleHours)));
        // COPQ
        Map<String, String> copqMap = getProjectCopqMap();
        if (CollectionUtils.isEmpty(copqMap)) {
            copqMap = copqByDayService.copqDay(new OmsNullLogger());
        }

        String ym = DateTime.now().toString("yyyyMM");

        for (RProjectInfo projectInfo : projectInfoList) {
            String pid = projectInfo.getPid();
            Double peopleMonth = peopleMonthsMap.get(pid);
            Double overtime = overtimeMap.get(pid);
            Double peopleHours = peopleHoursMap.get(pid);

            ProjectCostVo vo = new ProjectCostVo();
            vo.setBudget(projectInfo.getBudget());
            vo.setCopq(copqMap.get(pid));
            vo.setPeopleMonth(peopleMonth);
            vo.setShortName(projectInfo.getProjectShortName());
            vo.setCost(projectMap.get(pid));
            vo.setCostProfit(vo.getCost() * projectInfo.getProfitMargin());
            vo.setOvertime(overtime);
            vo.setPid(pid);
            vo.setNormal(NumberUtil.sub(peopleHours, overtime));
            list.add(vo);
        }
        return list;
    }

    private Map<String, String> getProjectCopqMap() {
        Object object = redisTemplate.opsForValue().get(RedisKeyContents.COPQ_DAY);
        if (object instanceof String json) {
            return JSONObject.parseObject(json, new TypeReference<HashMap<String, String>>() {
            });
        }
        return null;
    }

    @NotNull
    private static Map<String, Double> getProjectOvertimeList(List<ProjectCostVo> costList) {
        List<ProjectCostVo> overtimelist = new ArrayList<>();
        costList.stream().collect(Collectors.groupingBy(ProjectCostVo::getUserDate)).values().forEach(uList -> {
            int normal = 8;
            int size = uList.size();
            if (size == 1) {
                ProjectCostVo vo = uList.get(0);
                double peopleHours = vo.getPeopleHours();
                if (peopleHours > normal) {
                    vo.setOvertime(NumberUtil.sub(peopleHours, normal));
                    overtimelist.add(vo);
                }
            }
            // 写在两个项目中
            else {
                ProjectCostVo vo = new ProjectCostVo();
                double peoSum = uList.stream().mapToDouble(ProjectCostVo::getPeopleHours).sum();
                // 大于8小时算加班
                if (peoSum <= 8) {
                    return;
                }
                for (ProjectCostVo v : uList) {
                    double proportion = NumberUtil.div(v.getPeopleHours().doubleValue(), peoSum);
                    // 加班工时 = （已填工时 * 比例）-（正常工时 * 比例）
                    v.setOvertime(NumberUtil.sub(NumberUtil.mul(peoSum, proportion), NumberUtil.mul(normal, proportion)));
                    overtimelist.add(v);
                }
            }
        });
        Stream<ProjectCostVo> projectCostVoStream = overtimelist.stream().filter(e -> e.getOvertime() != null);
        return projectCostVoStream.collect(Collectors.groupingBy(ProjectCostVo::getPid, Collectors.summingDouble(ProjectCostVo::getOvertime)));
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
