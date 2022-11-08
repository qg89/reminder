package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.ReviewEcharts
 * @Description :
 * @date :  2022.11.07 17:07
 */
@Log4j2
public class WeeklyProjectUtils {

    /**
     * 评审问题创建图片
     *
     * @param vo
     * @return
     */
    public static File reviewQuestions(WeeklyProjectVo vo) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setAccessKey(vo.getAccessKey());
        projectInfo.setPKey(vo.getPKey());
        List<Issue> issues = WeeklyProjectRedmineUtils.reviewQuestion(projectInfo);
        Map<String, List<Issue>> weekNumMap = sortIssueList(issues);
        Set<String> categories = weekNumMap.keySet();
        // 变量
        String title = "评审问题数量";
        List<Integer> openList = new ArrayList<>();
        List<Integer> closeList = new ArrayList<>();
        weekNumMap.forEach((k, v) -> {
            closeList.add(v.stream().filter(e -> "Closed".equals(e.getStatusName())).toList().size());
            openList.add(v.stream().filter(e -> "New".equals(e.getStatusName())).toList().size());
        });

        // 模板参数
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("categories", JSON.toJSONString(categories));
        datas.put("open", JSON.toJSONString(openList));
        datas.put("close", JSON.toJSONString(closeList));
        datas.put("title", title);

        return EchartsUtil.getFile(datas, "reviewQuestions.ftl");
    }

    /**
     * 趋势
     *
     * @param vo
     * @return
     */
    public static File trends(WeeklyProjectVo vo) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setAccessKey(vo.getAccessKey());
        projectInfo.setPKey(vo.getPKey());
        List<Issue> issues = WeeklyProjectRedmineUtils.externalBugs(projectInfo);
        Map<String, List<Issue>> weekNumMap = sortIssueList(issues);
        List<String> categories = new ArrayList<>();
        // 变量
        String title = "Open Bug分布";
        List<Integer> data1 = new ArrayList<>();
        List<Integer> data2 = new ArrayList<>();

        weekNumMap.forEach((k, v) -> {
            data1.add(v.size());
            categories.add(DateUtil.thisYear() + "W" + k);
        });

        AtomicInteger value = new AtomicInteger();
        weekNumMap.forEach((k, v) -> {
            value.addAndGet(v.size());
            data2.add(value.intValue());
        });

        // 模板参数
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("categories", JSON.toJSONString(categories));
        datas.put("data1", JSON.toJSONString(data1));
        datas.put("data2", JSON.toJSONString(data2));
        datas.put("name1", "漏出BUG当周");
        datas.put("name2", "漏出BUG总");
        datas.put("title", title);
        datas.put("color1", "#0000ff");
        datas.put("color2", "#a4c2f4");
        return EchartsUtil.getFile(datas, "double-line.ftl");
    }

    /**
     * 根据周数排序
     *
     * @param issues
     * @return
     */
    private static Map<String, List<Issue>> sortIssueList(List<Issue> issues) {
        Map<String, List<Issue>> weekNumMap = issues.stream().collect(Collectors.groupingBy(e -> {
            Date createdOn = e.getCreatedOn();
            return String.valueOf(DateUtil.weekOfYear(createdOn) - 1);
        }));
        weekNumMap = weekNumMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (v1, v2) -> v1, LinkedHashMap::new));
        return weekNumMap;
    }
}
