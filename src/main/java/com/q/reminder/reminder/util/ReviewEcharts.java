package com.q.reminder.reminder.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.ReviewEcharts
 * @Description :
 * @date :  2022.11.07 17:07
 */
@Log4j2
public class ReviewEcharts {

    public static File createImageFile(WeeklyProjectVo vo) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl(vo.getRedmineUrl());
        projectInfo.setAccessKey(vo.getAccessKey());
        projectInfo.setPKey(vo.getPKey());
        List<Issue> issues = WeeklyProjectRedmineUtils.reviewQuestion(projectInfo);
        Map<String, List<Issue>> weekNumMap = issues.stream().collect(Collectors.groupingBy(e -> {
            Date createdOn = e.getCreatedOn();
            return String.valueOf(DateUtil.weekOfYear(createdOn));
        }));
        weekNumMap = weekNumMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (v1, v2) -> v1, LinkedHashMap::new));
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

        File base64 = EchartsUtil.getFile(datas, "reviewQuestions.ftl");
        return base64;
    }
}
