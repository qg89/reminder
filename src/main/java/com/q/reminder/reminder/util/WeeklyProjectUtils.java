package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.enums.CustomFieldsEnum;
import com.q.reminder.reminder.util.jfree.GenerateChartUtil;
import com.q.reminder.reminder.util.jfree.GeneratePieChartUtil;
import com.q.reminder.reminder.util.jfree.JFreeChartUtil;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.List;
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
public abstract class WeeklyProjectUtils {
    /**
     * 图例背景颜色列表
     */
    private final static List<Color> COLOR_ARRAY_LIST = new ArrayList<>(Arrays.asList(Color.RED, Color.YELLOW, Color.PINK, Color.GREEN, Color.BLUE));
    private final static List<String> BUG_LEVEL = List.of("S", "A", "B", "C", "D");

    /**
     * 评审问题创建图片
     *
     * @param vo
     * @return
     */
    public static File reviewQuestions(WeeklyProjectVo vo) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setPmKey(vo.getPmKey());
        projectInfo.setPKey(vo.getPKey());
        List<Issue> issues = WeeklyProjectRedmineUtils.reviewQuestion(projectInfo);
        Map<String, List<Issue>> weekNumMap = sortIssueMapByCreateOn(issues);
        List<String> categories = new ArrayList<>();
        // 变量
        String title = "评审问题数量";
        for (int i = 36; i < 52; i++) {
            String week = DateTime.now().toString("yy") + "W" + i;
            int weekOfYear = DateUtil.thisWeekOfYear();
            if (weekOfYear <= i + 1) {
                break;
            }
            categories.add(week);
        }

        //图例名称列表
        List<String> legendNameList = List.of("Closed数量", "Open数量");
        //数据列表
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> closeList = new ArrayList<>();
        List<Object> openList = new ArrayList<>();
        for (String category : categories) {
            List<Issue> issueList = weekNumMap.get(category);
            if (CollectionUtils.isEmpty(issueList)) {
                openList.add(0);
                closeList.add(0);
                continue;
            }
            List<Issue> closedList = issueList.stream().filter(e -> 5 != e.getStatusId()).toList();
            int colseSize = closedList.size();
            closeList.add(issueList.size() - colseSize);
            openList.add(colseSize);
        }
        dataList.add(closeList);
        dataList.add(openList);

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
        projectInfo.setPmKey(vo.getPmKey());
        projectInfo.setPKey(vo.getPKey());
        List<Issue> issues = WeeklyProjectRedmineUtils.externalBugs(projectInfo);
        Map<String, List<Issue>> weekNumMap = sortIssueMapByCreateOn(issues);
        List<String> categories = new ArrayList<>();
        // 变量
        String title = "线上问题每周增加情况";

        for (int i = 21; i < 52; i++) {
            String week = DateTime.now().toString("yy") + "W" + i;
            int weekOfYear = DateUtil.thisWeekOfYear();
            if (weekOfYear <= i + 1) {
                break;
            }
            categories.add(week);
        }

        //图例名称列表
        List<String> legendNameList = List.of("漏出BUG总数", "漏出BUG当周数");
        //数据列表
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> all = new ArrayList<>();
        List<Object> week = new ArrayList<>();
        int value = 0;
        for (String category : categories) {
            List<Issue> issueList = weekNumMap.get(category);
            if (CollectionUtils.isEmpty(issueList)) {
                week.add(0);
                all.add(value);
                continue;
            }
            int size = issueList.size();
            all.add(value = (size + value));
            week.add(size);
        }
        dataList.add(all);
        dataList.add(week);

        File file = null;
        try {
            URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            file = new File(url.getPath() + "/" + UUID.fastUUID() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            GenerateChartUtil.createLineChart(fileOutputStream, title, legendNameList, categories, dataList, JFreeChartUtil.createChartTheme(), "", "", 950, 500);
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
    private static Map<String, List<Issue>> sortIssueMapByCreateOn(List<Issue> issues) {
        Map<String, List<Issue>> weekNumMap = issues.stream().collect(Collectors.groupingBy(e ->
                DateTime.now().toString("yy") + "W" + (DateUtil.weekOfYear(e.getCreatedOn()) - 1)
        ));
        weekNumMap = weekNumMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (v1, v2) -> v1, LinkedHashMap::new));
        return weekNumMap;
    }

    private static Map<String, List<Issue>> sortIssueMapByLevel(List<Issue> issues) {
        Map<String, List<Issue>> weekNumMap = issues.stream().collect(Collectors.groupingBy(e -> e.getCustomFieldById(CustomFieldsEnum.BUG_LEVEL.getId()).getValue()));
        weekNumMap = weekNumMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (v1, v2) -> v1, LinkedHashMap::new));
        return weekNumMap;
    }

    /**
     * 根据周数排序
     *
     * @param timeEntries
     * @return
     */
    private static Map<String, List<TimeEntry>> sortTimeList(List<TimeEntry> timeEntries) {
        Map<String, List<TimeEntry>> weekNumMap = timeEntries.stream().collect(Collectors.groupingBy(e ->
                DateTime.now().toString("yy") + "W" + (DateUtil.weekOfYear(e.getSpentOn()) - 1)
        ));
        weekNumMap = weekNumMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (v1, v2) -> v1, LinkedHashMap::new));
        return weekNumMap;
    }

    /**
     * 全部BUG等级分布
     *
     * @param allBug
     * @return
     */
    public static File AllBugLevel(List<Issue> allBug) {
        // 变量
        String title = "ALL-Bug等级分布";

        Map<String, List<Issue>> levelMap = allBug.stream().collect(Collectors.groupingBy(e -> e.getCustomFieldById(CustomFieldsEnum.BUG_LEVEL.getId()).getValue()));
        //数据列表
        List<Object> dataList = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        BUG_LEVEL.forEach(level -> {
            List<Issue> issues = levelMap.get(level);
            if (CollectionUtils.isEmpty(issues)) {
                return;
            }
            dataList.add(issues.size());
            categories.add(level);
        });
        File file = null;
        try {
            URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            file = new File(url.getPath() + "/" + UUID.fastUUID() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            GeneratePieChartUtil.createPieChart(fileOutputStream, title, categories, dataList, 950, 500, JFreeChartUtil.createChartTheme(), COLOR_ARRAY_LIST);
        } catch (Exception e) {
            log.error(e);
        }
        if (file != null) {
            return file;
        }
        return null;
    }

    /**
     * openBug等级分布
     *
     * @param allBugList
     * @return
     */
    public static File openBug(List<Issue> allBugList) {
        // 变量
        String title = "Open Bug等级分布";

        Map<String, List<Issue>> levelMap = allBugList.stream().filter(e -> !"Closed".equals(e.getStatusName())).collect(Collectors.groupingBy(e -> e.getCustomFieldById(CustomFieldsEnum.BUG_LEVEL.getId()).getValue()));
        //数据列表
        List<Object> dataList = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        BUG_LEVEL.forEach(level -> {
            List<Issue> issues = levelMap.get(level);
            if (CollectionUtils.isEmpty(issues)) {
                return;
            }
            dataList.add(issues.size());
            categories.add(level);
        });
        File file = null;
        try {
            URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            file = new File(url.getPath() + "/" + UUID.fastUUID() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            GeneratePieChartUtil.createPieChart(fileOutputStream, title, categories, dataList, 950, 500, JFreeChartUtil.createChartTheme(), COLOR_ARRAY_LIST);
        } catch (Exception e) {
            log.error(e);
        }
        if (file != null) {
            return file;
        }
        return null;
    }

    /**
     * open总数大于15人员
     *
     * @param allBugList
     * @return
     */
    public static File openBug15(List<Issue> allBugList) {
        // 变量
        String title = "Open bug数量>15";

        List<Issue> issueList = allBugList.stream().filter(e -> !"Closed".equals(e.getStatusName())).toList();
        if (issueList.size() < 15) {
            log.info("[{}] 数据为空！", title);
            return null;
        }
        List<String> categories = new ArrayList<>();
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> s = new ArrayList<>();
        List<Object> a = new ArrayList<>();
        List<Object> b = new ArrayList<>();
        List<Object> c = new ArrayList<>();
        List<Object> d = new ArrayList<>();
        issueList.stream().collect(Collectors.groupingBy(e -> StringUtils.replace(e.getAssigneeName(), " ", ""))).forEach((name, list) -> {
            AtomicInteger sv = new AtomicInteger();
            AtomicInteger av = new AtomicInteger();
            AtomicInteger bv = new AtomicInteger();
            AtomicInteger cv = new AtomicInteger();
            AtomicInteger dv = new AtomicInteger();
            sortIssueMapByLevel(list).forEach((level, l) -> {
                Map<String, List<Issue>> levelMap = l.stream().collect(Collectors.groupingBy(e -> e.getCustomFieldById(CustomFieldsEnum.BUG_LEVEL.getId()).getValue()));
                switch (level) {
                    case "S":
                        List<Issue> ss = levelMap.get(level);
                        if (ss != null) {
                            sv.addAndGet(ss.size());
                        }
                        break;
                    case "A" :
                        List<Issue> as = levelMap.get(level);
                        if (as != null) {
                            av.addAndGet(as.size());
                        }
                        break;
                    case "B" :
                        List<Issue> bs = levelMap.get(level);
                        if (bs != null) {
                            bv.addAndGet(bs.size());
                        }
                        break;
                    case "C" :
                        List<Issue> cs = levelMap.get(level);
                        if (cs != null) {
                            cv.addAndGet(cs.size());
                        }
                        break;
                    case "D" :
                        List<Issue> ds = levelMap.get(level);
                        if (ds != null) {
                            dv.addAndGet(ds.size());
                        }
                        break;
                    default:
                        break;
                }
            });
            s.add(sv.intValue());
            a.add(av.intValue());
            b.add(bv.intValue());
            c.add(cv.intValue());
            d.add(dv.intValue());
            categories.add(name);
        });
        dataList.add(s);
        dataList.add(a);
        dataList.add(b);
        dataList.add(c);
        dataList.add(d);
        //数据列表
        File file = null;
        try {
            URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            file = new File(url.getPath() + "/" + UUID.fastUUID() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            GenerateChartUtil.createStackedBarChart(fileOutputStream, title, BUG_LEVEL, categories, dataList, JFreeChartUtil.createChartTheme(), "", "", 950, 500);
        } catch (Exception e) {
            log.error(e);
        }
        if (file != null) {
            return file;
        }

        return null;
    }

    /**
     * copq
     *
     * @param vo
     * @return
     */
    public static File copq(WeeklyProjectVo vo) {
        final int beginWeekNum = 33;
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setPmKey(vo.getPmKey());
        projectInfo.setPKey(vo.getPKey());
        Date sunday = getWeekNumToSunday(DateUtil.thisWeekOfYear() - 2);
        List<TimeEntry> timeEntryList = Objects.requireNonNull(WeeklyProjectRedmineUtils.wprojectTimes(projectInfo)).stream().filter(e -> e.getCreatedOn().before(sunday)).toList();
        List<TimeEntry> timeEntryBugs = Objects.requireNonNull(WeeklyProjectRedmineUtils.wprojectTimesBugs(projectInfo, "Bug")).stream().filter(e -> e.getCreatedOn().before(sunday)).toList();
        List<String> categories = new ArrayList<>();
        // 变量
        String title = "线上问题每周增加情况";
        Map<String, List<TimeEntry>> weekNumMap = sortTimeList(timeEntryList);
//        Map<String, List<TimeEntry>> weekNumBugsMap = sortTimeList(timeEntryBugs);

        //图例名称列表
        List<String> legendNameList = List.of("全部COPQ", "8月1日 COPQ");
        //数据列表
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> all = new ArrayList<>();
        List<Object> week = new ArrayList<>();
        for (int i = beginWeekNum; i < 52; i++) {
            String weekNum = DateTime.now().toString("yy") + "W" + i;
            int weekOfYear = DateUtil.thisWeekOfYear();
            if (weekOfYear <= i + 1) {
                break;
            }
            categories.add(weekNum);
        }

        for (String weekNum : categories) {
            List<TimeEntry> timeEntries = weekNumMap.get(weekNum);
            if (CollectionUtils.isEmpty(timeEntries)) {
                continue;
            }
            Date weekNumToSunday = getWeekNumToSunday(Integer.parseInt(weekNum.split("W")[1]));
            double allSum = timeEntryList.stream().filter(e ->
                    e.getSpentOn().before(weekNumToSunday)
            ).collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();
            double bugSum = timeEntryBugs.stream().filter(e ->
                    e.getSpentOn().before(weekNumToSunday)
            ).collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();

            // 8月1日之后的
            double allWeekSum = timeEntryList.stream().filter(e -> {
                Date spentOn = e.getSpentOn();
                return spentOn.before(weekNumToSunday) && new DateTime("2022-08-01").toDate().before(spentOn);
            }).collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();
            double bugWeekSum = timeEntryBugs.stream().filter(e -> {
                Date spentOn = e.getSpentOn();
                return spentOn.before(weekNumToSunday) && new DateTime("2022-08-01").toDate().before(spentOn);
            }).collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();

            if (allSum == 0) {
                allSum = 1;
            }
            all.add(BigDecimal.valueOf(bugSum / allSum * 100).setScale(2, RoundingMode.HALF_UP));
            if (allWeekSum == 0) {
                allWeekSum = 1;
            }
            week.add(BigDecimal.valueOf(bugWeekSum / allWeekSum * 100).setScale(2, RoundingMode.UP));
        }

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
     * 通过周  获取当前周的星期日
     *
     * @param weekNum
     * @return
     */
    private static Date getWeekNumToSunday(int weekNum) {
        WeekFields weekFields = WeekFields.ISO;
        //输入你想要的年份和周数
        LocalDate localDate = LocalDate.now().withYear(DateUtil.thisYear()).with(weekFields.weekOfYear(), weekNum);
        return Date.from(localDate.with(weekFields.dayOfWeek(), 7L).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().plus(1, ChronoUnit.DAYS).minusMillis(1));
    }
}
