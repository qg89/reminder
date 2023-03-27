package com.q.reminder.reminder.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.docx.v1.model.UpdateBlockRequest;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.constant.WeeklyReportConstants;
import com.q.reminder.reminder.service.WeeklyService;
import com.q.reminder.reminder.task.WeeklyProjectMonReportTask;
import com.q.reminder.reminder.util.ResourceUtils;
import com.q.reminder.reminder.util.WeeklyProjectRedmineUtils;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.WeeklyVo;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.q.reminder.reminder.util.WeeklyProjectUtils.getWeekNumToSunday;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.WeeklyServiceImpl
 * @Description :
 * @date :  2022.11.15 16:46
 */
@Log4j2
@Service
public class WeeklyServiceImpl implements WeeklyService {
    @Autowired
    private FeishuProperties feishuProperties;
    @Autowired
    private WeeklyProjectMonReportTask weeklyProjectMonReportTask;

    @Override
    public void resetReport(WeeklyVo vo) throws Exception {
        File logoFile = new File(ResourceUtils.path());
        vo.setAppSecret(feishuProperties.getAppSecret());
        vo.setAppId(feishuProperties.getAppId());
        Date startDay = vo.getStartDay();
        Date sunday = getWeekNumToSunday(vo.getWeekNum() - 1);
        String title = vo.getTitle();
        JSONArray jsonArray = BaseFeishu.cloud().documents().blocks(vo);
        ArrayList<UpdateBlockRequest> requests = new ArrayList<>();

        for (int i = 0; i < Objects.requireNonNull(jsonArray).size(); i++) {
            JSONObject block = JSONObject.parseObject(jsonArray.get(i).toString());
            Integer blockType = block.getInteger("block_type");
            if (4 == blockType) {
                String heading2 = JSONObject.parseObject(block.getJSONObject("heading2").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                if (WeeklyReportConstants.REVIEW_QUESTIONS.equals(heading2) && WeeklyReportConstants.REVIEW_QUESTIONS.equals(title)) {
                    weeklyProjectMonReportTask.reviewQuestions(vo, jsonArray, requests, i);
                    break;
                }
                if (WeeklyReportConstants.COPQ.equals(heading2) && WeeklyReportConstants.COPQ.equals(title)) {
                    weeklyProjectMonReportTask.copq(vo, jsonArray, requests, i);
                    break;
                }
            }
            if (5 == blockType) {
                String heading3 = JSONObject.parseObject(block.getJSONObject("heading3").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                if (WeeklyReportConstants.TRENDS.equals(heading3) && WeeklyReportConstants.TRENDS.equals(title)) {
                    weeklyProjectMonReportTask.tends(vo, jsonArray, requests, i);
                    break;
                }
                if (WeeklyReportConstants.BUG_LEVEL.equals(heading3) && WeeklyReportConstants.BUG_LEVEL.equals(title)) {
                    List<Issue> allBugList = WeeklyProjectRedmineUtils.OverallBug.allBug(vo).stream().filter(e -> {
                                            if (startDay == null) {
                                                return true;
                                            } else {
                                                return e.getCreatedOn().after(startDay) && e.getCreatedOn().before(sunday);
                                            }
                                        }).collect(Collectors.toList());
                    vo.setAllBugList(allBugList);
                    if ("All-Bug等级分布".equals(title)) {
                        weeklyProjectMonReportTask.allBugLevel(vo, jsonArray, requests, i);
                        break;
                    }
                    if ("Open-Bug等级分布".equals(title)) {
                        weeklyProjectMonReportTask.openBugLevel(vo, jsonArray, requests, i);
                        break;
                    }
                    if ("Open-Bug>15".equals(title)) {
                        weeklyProjectMonReportTask.openBug15(logoFile, vo, jsonArray, requests, i);
                        break;
                    }
                }
            }
        }
    }
}
