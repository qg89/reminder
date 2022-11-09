package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.util.jfree.GeneratePieChartUtil;
import com.q.reminder.reminder.util.jfree.JFreeChartUtil;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;

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
        HashMap<String, Object> datas = new HashMap<>(7);
        datas.put("categories", JSON.toJSONString(categories));
        datas.put("open", JSON.toJSONString(openList));
        datas.put("close", JSON.toJSONString(closeList));
        datas.put("title", title);
        datas.put("name1", "Closed数量");
        datas.put("name2", "Open数量");

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
        String title = "线上问题每周增加情况";
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

    public static File bugLevel(WeeklyProjectVo vo) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setAccessKey(vo.getAccessKey());
        projectInfo.setPKey(vo.getPKey());
        List<Issue> issues = WeeklyProjectRedmineUtils.OverallBug.bugLevelDistribution(projectInfo);

        // 变量
        String title = "ALL-Bug等级分布";

        Map<String, List<Issue>> levelMap = issues.stream().collect(Collectors.groupingBy(e -> e.getCustomFieldById(67).getValue()));
        levelMap.forEach((k, v) -> {

        });


        List<String> xAxisNameList = new ArrayList<>(Arrays.asList("S", "A", "B", "C", "D"));
        //数据列表
        List<Object> dataList1 = new ArrayList<>(Arrays.asList(1, 3, 5, 6, 2));
        //图例背景颜色列表
        List<Color> legendColorList = new ArrayList<>(Arrays.asList(Color.RED, Color.YELLOW, Color.PINK, Color.GREEN, Color.BLUE));
        //偏离百分比数据
        List<Double> explodePercentList = new ArrayList<>(Arrays.asList(0.1, 0.1, 0.1, 0.1, 0.1));
        File file = null;
        try {
            URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            file = new File(url.getPath() + "/" + UUID.fastUUID() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            GeneratePieChartUtil.createPieChart(fileOutputStream, title, xAxisNameList, dataList1
                    , 939, 488, JFreeChartUtil.createChartTheme(), legendColorList, explodePercentList);
        } catch (Exception e) {
            log.error(e);
        }
        if (file != null) {
            return file;
        }
        return null;
    }
}
