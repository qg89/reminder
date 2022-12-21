package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.q.reminder.reminder.contents.WeeklyReportContents;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.enums.CustomFieldsEnum;
import com.q.reminder.reminder.util.jfree.GenerateChartUtil;
import com.q.reminder.reminder.util.jfree.GeneratePieChartUtil;
import com.q.reminder.reminder.vo.ExcelVo;
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
import java.time.LocalDate;
import java.time.ZoneId;
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
    private final static String PATH = "/temp/file/";
    private final static String WIN_PATH = "d:/temp/file/";

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
    public static File reviewQuestions(WeeklyProjectVo vo) throws Exception {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setPmKey(vo.getPmKey());
        projectInfo.setPKey(vo.getPKey());
        String fileName = vo.getFileName();
        Date startDay = vo.getStartDay();
        Date sunday = getWeekNumToSunday(vo.getWeekNum() - 1);
        List<Issue> issues = WeeklyProjectRedmineUtils.reviewQuestion(projectInfo).stream().filter(e -> {
            if (startDay == null) {
                return true;
            } else {
                return e.getCreatedOn().after(startDay) && e.getCreatedOn().before(sunday);
            }
        }).collect(Collectors.toList());
        Map<String, List<Issue>> weekNumMap = sortIssueMapByCreateOn(issues);
        List<String> categories = new ArrayList<>();
        // 变量
        int thisWeekOfYear = DateUtil.thisWeekOfYear();
//        String title = "评审问题数量";
        String title = "\u8bc4\u5ba1\u95ee\u9898\u6570\u91cf";
        for (int i = 36; i < 52; i++) {
            String week = DateTime.now().toString("yy") + "W" + i;
            if (thisWeekOfYear <= i + 1) {
                break;
            }
            categories.add(week);
        }

        //图例名称列表
        List<String> legendNameList = List.of("Closed\u6570\u91cf", "Open\u6570\u91cf");
        //数据列表
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> closeList = new ArrayList<>();
        List<Object> openList = new ArrayList<>();

        List<ExcelVo> closedIssueList = new ArrayList<>();
        List<ExcelVo> openIssueList = new ArrayList<>();
        for (String category : categories) {
            List<Issue> issueList = weekNumMap.get(category);
            if (CollectionUtils.isEmpty(issueList)) {
                openList.add(0);
                closeList.add(0);
                continue;
            }
            List<Issue> oList = issueList.stream().filter(e -> 5 != e.getStatusId()).toList();
            int colseSize = oList.size();
            closeList.add(issueList.size() - colseSize);
            openList.add(colseSize);

            for (Issue issue : oList) {
                ExcelVo excelVo = new ExcelVo();
                excelVo.setAssignee(issue.getAssigneeName());
                excelVo.setWeekNum(category);
                excelVo.setSubject(issue.getSubject());
                excelVo.setStatus(issue.getStatusName());
                excelVo.setDescription(issue.getDescription());
                excelVo.setCreateTime(new DateTime(issue.getCreatedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                excelVo.setEndTime(new DateTime(issue.getClosedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                openIssueList.add(excelVo);
            }
            issues.stream().filter(e -> 5 == e.getStatusId()).forEach(issue -> {
                ExcelVo excelVo = new ExcelVo();
                excelVo.setAssignee(issue.getAssigneeName());
                excelVo.setWeekNum(category);
                excelVo.setSubject(issue.getSubject());
                excelVo.setStatus(issue.getStatusName());
                excelVo.setDescription(issue.getDescription());
                excelVo.setCreateTime(new DateTime(issue.getCreatedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                excelVo.setEndTime(new DateTime(issue.getClosedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                closedIssueList.add(excelVo);
            });

        }
        dataList.add(closeList);
        dataList.add(openList);
        Map<String, List<ExcelVo>> excelMap = new LinkedHashMap<>();
        excelMap.put("open", openIssueList);
        excelMap.put("closed", closedIssueList);
        WraterExcelSendFeishuUtil.wraterExcelSendFeishu(excelMap, vo, title);
        File file = new File(createDir() + fileName + "-" + title + ".jpeg");
        FileOutputStream out = new FileOutputStream(file);
        GenerateChartUtil.createStackedBarChart(out, title, legendNameList, categories, dataList, "", "", 950, 500);
        return file;
    }

    /**
     * 趋势
     *
     * @param vo
     * @return
     */
    public static File trends(WeeklyProjectVo vo) throws Exception {
        String fileName = vo.getFileName();
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setPmKey(vo.getPmKey());
        projectInfo.setPKey(vo.getPKey());
        Date startDay = vo.getStartDay();
        Date sunday = getWeekNumToSunday(vo.getWeekNum() - 1);
        List<Issue> issues = WeeklyProjectRedmineUtils.externalBugs(projectInfo).stream().filter(e -> {
            if (startDay == null) {
                return true;
            } else {
                return e.getCreatedOn().after(startDay) && e.getCreatedOn().before(sunday);
            }
        }).collect(Collectors.toList());
        Map<String, List<Issue>> weekNumMap = sortIssueMapByCreateOn(issues);
        List<String> categories = new ArrayList<>();
        // 变量
        String title = "线上问题每周增加情况";

        int thisWeekOfYear = DateUtil.thisWeekOfYear();
        for (int i = 21; i < 52; i++) {
            String week = DateTime.now().toString("yy") + "W" + i;
            if (thisWeekOfYear <= i + 1) {
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

        List<ExcelVo> allBugList = new ArrayList<>();
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
            issues.forEach(issue -> {
                ExcelVo excelVo = new ExcelVo();
                excelVo.setAssignee(issue.getAssigneeName());
                excelVo.setWeekNum(category);
                excelVo.setSubject(issue.getSubject());
                excelVo.setStatus(issue.getStatusName());
                excelVo.setDescription(issue.getDescription());
                excelVo.setCreateTime(new DateTime(issue.getCreatedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                excelVo.setEndTime(new DateTime(issue.getClosedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                allBugList.add(excelVo);
            });
        }
        dataList.add(all);
        dataList.add(week);
        Map<String, List<ExcelVo>> excelMap = new LinkedHashMap<>();
        excelMap.put("all", allBugList);
        WraterExcelSendFeishuUtil.wraterExcelSendFeishu(excelMap, vo, title);

        File file = new File(createDir() + fileName + "-" + title + ".jpeg");
        FileOutputStream out = new FileOutputStream(file);
        GenerateChartUtil.createLineChart(out, title, legendNameList, categories, dataList, "", "", 950, 500);
        return file;
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
     * @param vo
     * @return
     */
    public static File AllBugLevel(List<Issue> allBug, WeeklyProjectVo vo) throws Exception {
        String fileName = vo.getFileName();
        // 变量
        String title = "ALL-Bug等级分布";

        Map<String, List<Issue>> levelMap = allBug.stream().collect(Collectors.groupingBy(e -> e.getCustomFieldById(CustomFieldsEnum.BUG_LEVEL.getId()).getValue()));
        //数据列表
        List<Object> dataList = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        List<ExcelVo> sList = new ArrayList<>();
        List<ExcelVo> aList = new ArrayList<>();
        List<ExcelVo> bList = new ArrayList<>();
        List<ExcelVo> cList = new ArrayList<>();
        List<ExcelVo> dList = new ArrayList<>();
        BUG_LEVEL.forEach(level -> {
            List<Issue> issues = levelMap.get(level);
            if (CollectionUtils.isEmpty(issues)) {
                return;
            }
            dataList.add(issues.size());
            categories.add(level);
            issues.forEach(issue -> {
                ExcelVo excelVo = new ExcelVo();
                excelVo.setAssignee(issue.getAssigneeName());
                excelVo.setWeekNum(level);
                excelVo.setSubject(issue.getSubject());
                excelVo.setStatus(issue.getStatusName());
                excelVo.setDescription(issue.getDescription());
                excelVo.setCreateTime(new DateTime(issue.getCreatedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                excelVo.setEndTime(new DateTime(issue.getClosedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                if ("S".equals(level)) {
                    sList.add(excelVo);
                }
                if ("A".equals(level)) {
                    aList.add(excelVo);
                }
                if ("B".equals(level)) {
                    bList.add(excelVo);
                }
                if ("C".equals(level)) {
                    cList.add(excelVo);
                }
                if ("D".equals(level)) {
                    dList.add(excelVo);
                }
            });
        });

        Map<String, List<ExcelVo>> excelMap = new LinkedHashMap<>();
        excelMap.put("s", sList);
        excelMap.put("a", aList);
        excelMap.put("b", bList);
        excelMap.put("c", cList);
        excelMap.put("d", dList);
        WraterExcelSendFeishuUtil.wraterExcelSendFeishu(excelMap, vo, title);

        File file = new File(createDir() + fileName + "-" + title + ".jpeg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        GeneratePieChartUtil.createPieChart(fileOutputStream, title, categories, dataList, 950, 500, COLOR_ARRAY_LIST);

        return file;
    }

    /**
     * openBug等级分布
     *
     * @param allBugList
     * @param vo
     * @return
     */
    public static File openBug(List<Issue> allBugList, WeeklyProjectVo vo) throws Exception {
        String fileName = vo.getFileName();
        // 变量
        String title = "Open Bug等级分布";

        Map<String, List<Issue>> levelMap = allBugList.stream().filter(e -> !"Closed".equals(e.getStatusName())).collect(Collectors.groupingBy(e -> e.getCustomFieldById(CustomFieldsEnum.BUG_LEVEL.getId()).getValue()));
        //数据列表
        List<Object> dataList = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        List<ExcelVo> sList = new ArrayList<>();
        List<ExcelVo> aList = new ArrayList<>();
        List<ExcelVo> bList = new ArrayList<>();
        List<ExcelVo> cList = new ArrayList<>();
        List<ExcelVo> dList = new ArrayList<>();
        BUG_LEVEL.forEach(level -> {
            List<Issue> issues = levelMap.get(level);
            if (CollectionUtils.isEmpty(issues)) {
                return;
            }
            dataList.add(issues.size());
            categories.add(level);

            issues.forEach(issue -> {
                ExcelVo excelVo = new ExcelVo();
                excelVo.setAssignee(issue.getAssigneeName());
                excelVo.setWeekNum(level);
                excelVo.setSubject(issue.getSubject());
                excelVo.setStatus(issue.getStatusName());
                excelVo.setDescription(issue.getDescription());
                excelVo.setCreateTime(new DateTime(issue.getCreatedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                excelVo.setEndTime(new DateTime(issue.getClosedOn()).toString("yyyy-MM-dd HH:mm:ss"));
                if ("S".equals(level)) {
                    sList.add(excelVo);
                }
                if ("A".equals(level)) {
                    aList.add(excelVo);
                }
                if ("B".equals(level)) {
                    bList.add(excelVo);
                }
                if ("C".equals(level)) {
                    cList.add(excelVo);
                }
                if ("D".equals(level)) {
                    dList.add(excelVo);
                }
            });
        });

        Map<String, List<ExcelVo>> excelMap = new LinkedHashMap<>();
        excelMap.put("s", sList);
        excelMap.put("a", aList);
        excelMap.put("b", bList);
        excelMap.put("c", cList);
        excelMap.put("d", dList);
        WraterExcelSendFeishuUtil.wraterExcelSendFeishu(excelMap, vo, title);
        File file = new File(createDir() + fileName + "-" + title + ".jpeg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        GeneratePieChartUtil.createPieChart(fileOutputStream, title, categories, dataList, 950, 500, COLOR_ARRAY_LIST);

        return file;
    }

    /**
     * open总数大于15人员
     *
     * @param allBugList
     * @return
     */
    public static File openBug15(List<Issue> allBugList) throws Exception {
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
                    case "A":
                        List<Issue> as = levelMap.get(level);
                        if (as != null) {
                            av.addAndGet(as.size());
                        }
                        break;
                    case "B":
                        List<Issue> bs = levelMap.get(level);
                        if (bs != null) {
                            bv.addAndGet(bs.size());
                        }
                        break;
                    case "C":
                        List<Issue> cs = levelMap.get(level);
                        if (cs != null) {
                            cv.addAndGet(cs.size());
                        }
                        break;
                    case "D":
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
        File file = new File(createDir() + UUID.fastUUID() + ".jpeg");
        FileOutputStream out = new FileOutputStream(file);
        GenerateChartUtil.createStackedBarChart(out, title, BUG_LEVEL, categories, dataList, "", "", 950, 500);
        return file;
    }

    /**
     * copq
     *
     * @param vo
     * @return
     */
    public static File copq(WeeklyProjectVo vo) throws Exception {
        int weekNum = vo.getWeekNum();
        String fileName = vo.getFileName();
        int beginWeekNum = 33;
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setPmKey(vo.getPmKey());
        projectInfo.setPKey(vo.getPKey());
        Date startDay = vo.getStartDay();
        boolean isStart = startDay != null;
        Date sunday = getWeekNumToSunday(weekNum - 1);
        List<TimeEntry> timeEntryList = Objects.requireNonNull(WeeklyProjectRedmineUtils.wProjectTimes(projectInfo)).stream().filter(e ->
                isStart && e.getSpentOn().after(startDay) && e.getSpentOn().before(sunday)
        ).toList();
        List<TimeEntry> timeEntryBugsList = WeeklyProjectRedmineUtils.wprojectTimesBugs(projectInfo);
        List<TimeEntry> timeEntryBugs = Objects.requireNonNull(timeEntryBugsList).stream().filter(e ->
                isStart && e.getSpentOn().after(startDay) && e.getSpentOn().before(sunday)
        ).toList();
        List<String> categories = new ArrayList<>();
        // 变量
        String title = WeeklyReportContents.COPQ;
//        Map<String, List<TimeEntry>> weekNumMap = sortTimeList(timeEntryList);
//        Map<String, List<TimeEntry>> weekNumBugsMap = sortTimeList(timeEntryBugs);

        //图例名称列表
        List<String> legendNameList = List.of("全部COPQ", "8月1日 COPQ");
        //数据列表
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> all = new ArrayList<>();
        List<Object> week = new ArrayList<>();
        String yy = DateTime.now().toString("yy");

        // 判断开始日期
        if (isStart) {
            beginWeekNum = DateUtil.weekOfYear(startDay) - 1;
        }

        for (int i = beginWeekNum; i < weekNum; i++) {
            String weekOfYear = yy + "W" + i;
            categories.add(weekOfYear);
        }
        Map<String, List<ExcelVo.ExcelTimeVo>> map = new LinkedHashMap<>();

        List<ExcelVo.ExcelTimeVo> copqList = new ArrayList<>();
        for (String weekOfYear : categories) {
//            List<TimeEntry> timeEntries = weekNumMap.get(weekOfYear);
//            if (CollectionUtils.isEmpty(timeEntries)) {
//                all.add(0.00D);
//                week.add(0.00D);
//                continue;
//            }
            int thisWeek = Integer.parseInt(weekOfYear.split("W")[1]);
            Date weekNumToSunday = getWeekNumToSunday(thisWeek);
            double allSum = timeEntryList.stream().filter(e -> e.getSpentOn().before(weekNumToSunday)).collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();
            double bugSum = timeEntryBugs.stream().filter(e -> e.getSpentOn().before(weekNumToSunday)).collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();

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
            double allV = BigDecimal.valueOf(bugSum / allSum * 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
            all.add(allV);
            if (allWeekSum == 0) {
                allWeekSum = 1;
            }
            double weekV = BigDecimal.valueOf(bugWeekSum / allWeekSum * 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
            week.add(weekV);

            ExcelVo.ExcelTimeVo vAll = new ExcelVo.ExcelTimeVo();
            vAll.setAll(allSum);
            vAll.setBug(bugSum);
            vAll.setAll81(allWeekSum);
            vAll.setBug81(bugWeekSum);
            vAll.setWeekNum(String.valueOf(thisWeek));
            copqList.add(vAll);
        }

        dataList.add(all);
        dataList.add(week);

        map.put("copq", copqList);
        WraterExcelSendFeishuUtil.wraterExcelTimeSendFeishu(map, vo, title);

        File file = new File(createDir() + fileName + "-" + title + ".jpeg");
        FileOutputStream out = new FileOutputStream(file);
        GenerateChartUtil.createLineChart(out, title, legendNameList, categories, dataList, "", "", 950, 500);
        return file;
    }

    /**
     * 通过周  获取当前周的星期日
     *
     * @param weekNum
     * @return
     */
    public static Date getWeekNumToSunday(int weekNum) {
        WeekFields weekFields = WeekFields.ISO;
        //输入你想要的年份和周数
        LocalDate localDate = LocalDate.now().withYear(DateUtil.thisYear()).with(weekFields.weekOfYear(), weekNum);
        return Date.from(localDate.with(weekFields.dayOfWeek(), 7L).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 创建文件夹
     *
     * @return
     */
    public static String createDir() {
        String path = WIN_PATH;
        if (SystemUtils.isLinux()) {
            path = PATH;
        }
        if (SystemUtils.isWindows()) {
            path = WIN_PATH;
        }

        File f = new File(path);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
        return path;
    }
}
