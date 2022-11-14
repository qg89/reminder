package com.q.reminder.reminder.task;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.docx.v1.model.ReplaceImageRequest;
import com.lark.oapi.service.docx.v1.model.UpdateBlockRequest;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.contents.WeeklyReportContents;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.util.*;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.bean.Issue;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.WeeklyProjectMonReportHandle
 * @Description : 周一中午12点 写日报
 * @date :  2022.11.01 17:55
 */
@Component
@Log4j2
public class WeeklyProjectMonReportTask {
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
        String jobParam = XxlJobHelper.getJobParam();
        int weekNumber = 0;
        if (StringUtils.isNotBlank(jobParam) && NumberUtil.isInteger(jobParam)) {
            weekNumber = Integer.parseInt(jobParam);
        }
        List<WeeklyProjectVo> list = projectInfoService.getWeeklyDocxList(weekNumber);
        this.writeReport(list);
        return ReturnT.SUCCESS;
    }

    private void writeReport(List<WeeklyProjectVo> list) {
        list.forEach(report -> {
            String redmineUrl = report.getRedmineUrl();
            String accessKey = report.getPmKey();
            String pKey = report.getPKey();
            String pmOu = report.getPmOu();
            WeeklyProjectVo vo = new WeeklyProjectVo();
            String appId = feishuProperties.getAppId();
            vo.setAppId(appId);
            vo.setAppSecret(feishuProperties.getAppSecret());
            vo.setFileToken(report.getFileToken());
            vo.setRedmineUrl(redmineUrl);
            vo.setPmKey(accessKey);
            vo.setPKey(pKey);

            ProjectInfo projectInfo = new ProjectInfo();
            projectInfo.setRedmineUrl(redmineUrl);
            projectInfo.setPmKey(accessKey);
            projectInfo.setPKey(pKey);
            List<Issue> allBugList = WeeklyProjectRedmineUtils.OverallBug.allBug(projectInfo);

            JSONArray jsonArray = WeeklyProjectFeishuUtils.blocks(vo);
            ArrayList<UpdateBlockRequest> requests = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(jsonArray).size(); i++) {
                JSONObject block = JSONObject.parseObject(jsonArray.get(i).toString());
                Integer blockType = block.getInteger("block_type");
                if (4 == blockType) {
                    String heading2 = JSONObject.parseObject(block.getJSONObject("heading2").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                    if (WeeklyReportContents.REVIEW_QUESTIONS.equals(heading2)) {
                        block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
                        vo.setBlockId(block.getString("block_id"));
                        // 评审问题
                        File file = WeeklyProjectUtils.reviewQuestions(vo);
                        if (file != null) {
                            addRequests(vo, file, requests);
                            log.info("[{}]项目周报，评审问题 执行完成", report.getProjectShortName());
                        }
                    }
                    if (WeeklyReportContents.COPQ.equals(heading2)) {
                        block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
                        vo.setBlockId(block.getString("block_id"));
                        // 评审问题
                        File file = WeeklyProjectUtils.copq(vo);
                        if (file != null) {
                            addRequests(vo, file, requests);
                            log.info("{}项目周报，COPQ 执行完成", report.getProjectShortName());
                        }
                    }
                }
                if (5 == blockType) {
                    String heading3 = JSONObject.parseObject(block.getJSONObject("heading3").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                    if (WeeklyReportContents.TRENDS.equals(heading3)) {
                        block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
                        // 趋势
                        vo.setBlockId(block.getString("block_id"));
                        File file = WeeklyProjectUtils.trends(vo);
                        if (file != null) {
                            addRequests(vo, file, requests);
                            log.info("[{}]项目周报，趋势 执行完成", report.getProjectShortName());
                        }
                    }
                    if (WeeklyReportContents.BUG_LEVEL.equals(heading3)) {
                        block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
                        // All-bug等级
                        vo.setBlockId(block.getString("block_id"));
                        File bugLevel = WeeklyProjectUtils.AllBugLevel(allBugList);
                        if (bugLevel != null) {
                            addRequests(vo, bugLevel, requests);
                            log.info("[{}]项目周报，All-bug等级分布 执行完成", report.getProjectShortName());
                        }
                        block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
                        // Open-Bug等级
                        vo.setBlockId(block.getString("block_id"));
                        File openBug = WeeklyProjectUtils.openBug(allBugList);
                        if (openBug != null) {
                            addRequests(vo, openBug, requests);
                            log.info("[{}]项目周报，Open-Bug等级分布 执行完成", report.getProjectShortName());
                        }
                        block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
                        // open-Bug >15
                        vo.setBlockId(block.getString("block_id"));
                        File openBug15 = WeeklyProjectUtils.openBug15(allBugList);
                        if (openBug15 != null) {
                            addRequests(vo, openBug15, requests);
                            log.info("[{}]项目周报，open-Bug >15 执行完成", report.getProjectShortName());
                        }
                    }
                }
            }
            FeishuJavaUtils.batchUpdateBlocks(vo, requests.toArray(new UpdateBlockRequest[0]));
            log.info("[{}]项目周报更新完成", report.getProjectShortName());

            String secret = FeiShuApi.getSecret(appId, feishuProperties.getAppSecret());
            FeiShuApi.sendText(pmOu,"", secret);
        });
        File file = new File(ResourceUtils.path("templates/file"));
        for (File f : Objects.requireNonNull(file.listFiles(((dir, name) -> name.endsWith(".png") || new File(name).isDirectory())))) {
            f.delete();
        }
    }

    private void addRequests(WeeklyProjectVo vo, File file, List<UpdateBlockRequest> requests) {
        if (file == null) {
            return;
        }
        String blockId = vo.getBlockId();
        // 通过飞书上传图片至对应的block_id下
        FeishuUploadImageVo imageVo = new FeishuUploadImageVo();
        imageVo.setParentType("docx_image");
        imageVo.setFile(file);
        imageVo.setFileName(file.getName());
        imageVo.setSize(file.length());
        imageVo.setAppSecret(vo.getAppSecret());
        imageVo.setAppId(vo.getAppId());
        imageVo.setParentNode(blockId);
        String fileToken = FeishuJavaUtils.upload(imageVo);
        if (StringUtils.isBlank(fileToken)) {
            log.info("飞书上传素材返回为空");
            return;
        }
        UpdateBlockRequest update = UpdateBlockRequest.newBuilder().build();
        update.setReplaceImage(ReplaceImageRequest.newBuilder()
                .token(fileToken)
                .build());
        update.setBlockId(blockId);
        requests.add(update);
    }

    /**
     * 替换图片
     *
     * @param vo
     */
    private void replaceImaage(WeeklyProjectVo vo, File file) {
        if (file == null) {
            return;
        }
        // 通过飞书上传图片至对应的block_id下
        FeishuUploadImageVo imageVo = new FeishuUploadImageVo();
        imageVo.setParentType("docx_image");
        imageVo.setFile(file);
        imageVo.setFileName(file.getName());
        imageVo.setSize(file.length());
        imageVo.setAppSecret(vo.getAppSecret());
        imageVo.setAppId(vo.getAppId());
        imageVo.setParentNode(vo.getBlockId());
        String fileToken = FeishuJavaUtils.upload(imageVo);
        vo.setImageToken(fileToken);
        // 通过飞书替换图片至block_id
        Boolean updateBlocks = FeishuJavaUtils.updateBlocks(vo);
        if (!updateBlocks) {
            System.out.println();
        }
        // 删除图片
        try {

        } finally {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
}
