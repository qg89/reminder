package com.q.reminder.reminder.handle;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.contents.WeeklyReportContents;
import com.q.reminder.reminder.handle.base.HoldayBase;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.util.ReviewEcharts;
import com.q.reminder.reminder.util.WeeklyProjectFeishuUtils;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.WeeklyProjectMonReportHandle
 * @Description : 周一中午12点 写日报
 * @date :  2022.11.01 17:55
 */
@Component
@Log4j2
public class WeeklyProjectMonReportHandle {
    @Autowired
    private HoldayBase holdayBase;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private FeishuProperties feishuProperties;

    @XxlJob("weekProjectMonReport")
    public ReturnT<String> weekProjectMonReport() {
        if (holdayBase.queryHoliday()) {
            log.info("节假日放假!!!!");
            return ReturnT.SUCCESS;
        }
        List<WeeklyProjectVo> list = projectInfoService.getWeeklyDocxList();
        this.writeReport(list);
        return ReturnT.SUCCESS;
    }

    private void writeReport(List<WeeklyProjectVo> list) {
        list.forEach(report -> {
            WeeklyProjectVo vo = new WeeklyProjectVo();
            vo.setAppId(feishuProperties.getAppId());
            vo.setAppSecret(feishuProperties.getAppSecret());
            vo.setFileToken(report.getFileToken());
            vo.setRedmineUrl(report.getRedmineUrl());
            vo.setAccessKey(report.getAccessKey());
            vo.setPKey(report.getPKey());

            List<Object> blocks = Objects.requireNonNull(WeeklyProjectFeishuUtils.blocks(vo)).stream().filter(e -> {
                JSONObject block = JSONObject.parseObject(e.toString());
                Integer blockType = block.getInteger("block_type");
                return 14 == blockType;
            }).toList();
            blocks.forEach(e -> {
                JSONObject block = JSONObject.parseObject(e.toString());
                String content = JSONObject.parseObject(block.getJSONObject("code").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                if (WeeklyReportContents.REVIEW_QUESTIONS.equals(content)) {
                    // 评审问题
                    String blockId = block.getString("block_id");
                    vo.setBlockId(blockId);
                    reviewQuestions(vo);
                }
            });
        });
    }

    private void reviewQuestions(WeeklyProjectVo vo) {
        // 先通过redmine生成图片
        File file = ReviewEcharts.createImageFile(vo);
        // 通过飞书上传图片至对应的block_id下

        // 通过飞书替换图片至block_id
    }
}
