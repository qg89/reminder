package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.util.jfree.GenerateChartUtil;
import com.q.reminder.reminder.util.jfree.GeneratePieChartUtil;
import com.q.reminder.reminder.util.jfree.JFreeChartUtil;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
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
public abstract class WeeklyProjectUtils {

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
        List<String> categories = new ArrayList<>();
        // 变量
        String title = "评审问题数量";
        weekNumMap.forEach((k, v) -> {
            categories.add(DateTime.now().toString("yy") + "W" + k);
        });

        //图例名称列表
        List<String> legendNameList = List.of("Closed数量", "Open数量");
        //数据列表
        List<List<Object>> dataList = new ArrayList<>();
        AtomicInteger value = new AtomicInteger();
        List<Object> all = new ArrayList<>();
        List<Object> week = new ArrayList<>();
        weekNumMap.forEach((k, v) -> {
            value.addAndGet(v.size());
            all.add(value.intValue());
            week.add(v.size());
        });
        dataList.add(all);
        dataList.add(week);

        File file = null;
        try {
            URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            file = new File(url.getPath() + "/" + UUID.fastUUID() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            GenerateChartUtil.createStackedBarChart(fileOutputStream, title, legendNameList, categories, dataList, JFreeChartUtil.createChartTheme(), "", "", 950, 500);
        } catch (Exception e) {
            log.error(e);
        }
        if (file != null) {
            return file;
        }
        return null;
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
        String title = "线上问题每周增加情况";

        weekNumMap.forEach((k, v) -> {
            categories.add(DateTime.now().toString("yy") + "W" + k);
        });


        //图例名称列表
        List<String> legendNameList = List.of("漏出BUG总数", "漏出BUG当周数");
        //数据列表
        List<List<Object>> dataList = new ArrayList<>();
        AtomicInteger value = new AtomicInteger();
        List<Object> all = new ArrayList<>();
        List<Object> week = new ArrayList<>();
        weekNumMap.forEach((k, v) -> {
            value.addAndGet(v.size());
            all.add(value.intValue());
            week.add(v.size());
        });
        dataList.add(all);
        dataList.add(week);

        File file = null;
        try {
            URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            file = new File(url.getPath() + "/" + UUID.fastUUID() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            GenerateChartUtil.createLineChart(fileOutputStream, title, legendNameList, categories
                    , dataList, JFreeChartUtil.createChartTheme(), "", "", 950, 500);
        } catch (Exception e) {
            log.error(e);
        }
        if (file != null) {
            return file;
        }
        return null;
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

    public static File bugLevel(WeeklyProjectVo vo) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setAccessKey(vo.getAccessKey());
        projectInfo.setPKey(vo.getPKey());
        List<Issue> issues = WeeklyProjectRedmineUtils.OverallBug.bugLevelDistribution(projectInfo);

        // 变量
        String title = "ALL-Bug等级分布";


        List<String> xAxisNameList = new ArrayList<>();
        //图例背景颜色列表
        List<Color> legendColorList = new ArrayList<>(Arrays.asList(Color.RED, Color.YELLOW, Color.PINK, Color.GREEN, Color.BLUE));
        Map<String, List<Issue>> levelMap = issues.stream().collect(Collectors.groupingBy(e -> e.getCustomFieldById(67).getValue()));
        //数据列表
        List<Object> dataList = new ArrayList<>();
        levelMap.forEach((k, v) -> {
            xAxisNameList.add(k);
            dataList.add(v.size());
        });
        File file = null;
        try {
            URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            file = new File(url.getPath() + "/" + UUID.fastUUID() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            GeneratePieChartUtil.createPieChart(fileOutputStream, title, xAxisNameList, dataList, 950, 500, JFreeChartUtil.createChartTheme(), legendColorList);
        } catch (Exception e) {
            log.error(e);
        }
        if (file != null) {
            return file;
        }
        return null;
    }
}
