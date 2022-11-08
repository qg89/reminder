package com.q.reminder.reminder.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.test.EchartsUtil;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.bean.Issue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.ReviewEcharts
 * @Description :
 * @date :  2022.11.07 17:07
 */
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

        // 生成option字符串
        String option = FreemarkerUtil.generate("reviewQuestions.ftl", datas);

        // 根据option参数
        String base64 = null;
        try {
            base64 = EchartsUtil.generateEchartsBase64(option);
            return generateImage(base64, "D:\\dev-work\\my-work\\reminder\\src\\main\\resources\\template\\file\\test.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File base64ToFile(String base64) {
        if (base64 == null || "".equals(base64)) {
            return null;
        }
        byte[] buff = Base64.decode(base64);
        File file = null;
        FileOutputStream fout = null;
        try {
            file = File.createTempFile("D:\\dev-work\\my-work\\reminder\\src\\main\\resources\\template\\file", "png");
            fout = new FileOutputStream(file);
            fout.write(buff);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public static File generateImage(String base64, String path) throws IOException {
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        File file = new File(path);
        try {
            byte[] b = Base64Decoder.decode(base64);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(b);
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return file;
    }

    public static void main(String[] args) {
        WeeklyProjectVo vo = new WeeklyProjectVo();
        File file = createImageFile(vo);
    }
}
