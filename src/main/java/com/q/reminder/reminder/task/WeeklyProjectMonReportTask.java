package com.q.reminder.reminder.task;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.service.docx.v1.model.ReplaceImageRequest;
import com.lark.oapi.service.docx.v1.model.UpdateBlockRequest;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.constant.WeeklyReportConstants;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.util.*;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;
import com.q.reminder.reminder.vo.SendVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.q.reminder.reminder.vo.WeeklyVo;
import com.taskadapter.redmineapi.bean.Issue;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.q.reminder.reminder.util.WeeklyProjectUtils.getWeekNumToSunday;

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
    @Autowired
    private Client client;

    @XxlJob("weekProjectMonReport")
    public ReturnT<String> weekProjectMonReport() throws Exception {
        ReturnT<String> ret = new ReturnT<>(null);
        if (holdayBase.queryHoliday()) {
            ret.setMsg("节假日放假!!!!");
            return ret;
        }
        String jobParam = XxlJobHelper.getJobParam();
        int weekNumber = 0;
        String id = null;
        if (StringUtils.isNotBlank(jobParam)) {
            String[] param = jobParam.split(",");
            String weekNum = param[0];
            if (StringUtils.isNotBlank(weekNum) && NumberUtil.isInteger(weekNum)) {
                weekNumber = Integer.parseInt(weekNum);
            }
            if (param.length > 1 && StringUtils.isNotBlank(param[1])) {
                id = param[1];
            }
        }
        List<WeeklyProjectVo> list = projectInfoService.getWeeklyDocxList(weekNumber, id);
        this.writeReport(list);
        return ret;
    }

    private void writeReport(List<WeeklyProjectVo> list) throws Exception {
        File logoFile = new File(ResourceUtils.path());
        for (WeeklyProjectVo report : list) {
            Date sunday = getWeekNumToSunday(report.getWeekNum() - 1);
            String redmineUrl = report.getRedmineUrl();
            String accessKey = report.getPmKey();
            String pKey = report.getPKey();
            String projectShortName = report.getProjectShortName();
            String fileToken = report.getFileToken();
            String fileName = report.getFileName();
            Integer weekNum = report.getWeekNum();
            Date startDay = report.getStartDay();
            WeeklyProjectVo vo = new WeeklyProjectVo();
            String appId = feishuProperties.getAppId();
            vo.setAppId(appId);
            vo.setAppSecret(feishuProperties.getAppSecret());
            vo.setFileToken(fileToken);
            vo.setRedmineUrl(redmineUrl);
            vo.setPmKey(accessKey);
            vo.setPKey(pKey);
            vo.setProjectShortName(projectShortName);
            vo.setFileName(fileName);
            vo.setWeekNum(weekNum);
            vo.setStartDay(report.getStartDay());
            vo.setPmOu(report.getPmOu());
            List<Issue> allBugList = WeeklyProjectRedmineUtils.OverallBug.allBug(vo).stream().filter(e -> {
                if (startDay == null) {
                    return true;
                } else {
                    return e.getCreatedOn().after(startDay) && e.getCreatedOn().before(sunday);
                }
            }).collect(Collectors.toList());
            vo.setAllBugList(allBugList);

            JSONArray jsonArray = WeeklyProjectFeishuUtils.blocks(vo);
            ArrayList<UpdateBlockRequest> requests = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(jsonArray).size(); i++) {
                JSONObject block = JSONObject.parseObject(jsonArray.get(i).toString());
                Integer blockType = block.getInteger("block_type");
                if (4 == blockType) {
                    String heading2 = JSONObject.parseObject(block.getJSONObject("heading2").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                    if (WeeklyReportConstants.REVIEW_QUESTIONS.equals(heading2)) {
                        i = reviewQuestions(vo, jsonArray, requests, i);
                    }
                    if (WeeklyReportConstants.COPQ.equals(heading2)) {
                        i = copq(vo, jsonArray, requests, i);
                    }
                }
                if (5 == blockType) {
                    String heading3 = JSONObject.parseObject(block.getJSONObject("heading3").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                    if (WeeklyReportConstants.TRENDS.equals(heading3)) {
                        i = tends(vo, jsonArray, requests, i);
                    }
                    if (WeeklyReportConstants.BUG_LEVEL.equals(heading3)) {
                        i = allBugLevel(vo, jsonArray, requests, i);
                        i = openBugLevel(vo, jsonArray, requests, i);
                        i = openBug15(logoFile, vo, jsonArray, requests, i);
                    }
                }
            }
            BaseFeishu.block(client).batchUpdateBlocks(vo, requests.toArray(new UpdateBlockRequest[0]));
            log.info("[{}]项目周报更新完成", projectShortName);

            sendFeishu(report);
        }
    }

    /**
     * 评审问题
     *
     * @param vo
     * @param jsonArray
     * @param requests
     * @param i
     * @return
     */
    public int reviewQuestions(WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
        vo.setBlockId(block.getString("block_id"));
        // 评审问题
        File file = WeeklyProjectUtils.reviewQuestions(vo);
        addRequests(vo, file, requests);
        log.info("[{}]项目周报，评审问题 执行完成", vo.getProjectShortName());
        return i;
    }

    /**
     * open-Bug >15
     *
     * @param logoFile
     * @param vo
     * @param jsonArray
     * @param requests
     * @param i
     * @return
     */
    public int openBug15(File logoFile, WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i) throws Exception {
        JSONObject block;
        block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
        // open-Bug >15
        vo.setBlockId(block.getString("block_id"));
        File openBug15 = WeeklyProjectUtils.openBug15(vo.getAllBugList());
        if (openBug15 == null) {
            openBug15 = logoFile;
        }
        addRequests(vo, openBug15, requests);
        log.info("[{}]项目周报，open-Bug >15 执行完成", vo.getProjectShortName());
        return i;
    }

    /**
     * Open-Bug等级分布
     *
     * @param vo
     * @param jsonArray
     * @param requests
     * @param i
     * @return
     */
    public int openBugLevel(WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
        // Open-Bug等级
        vo.setBlockId(block.getString("block_id"));
        File openBug = WeeklyProjectUtils.openBug(vo.getAllBugList(), vo);
        addRequests(vo, openBug, requests);
        log.info("[{}]项目周报，Open-Bug等级分布 执行完成", vo.getProjectShortName());
        return i;
    }

    /**
     * All-bug等级分布
     *
     * @param vo
     * @param jsonArray
     * @param requests
     * @param i
     * @return
     */
    public int allBugLevel(WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
        // All-bug等级
        vo.setBlockId(block.getString("block_id"));
        File bugLevel = WeeklyProjectUtils.AllBugLevel(vo.getAllBugList(), vo);
        addRequests(vo, bugLevel, requests);
        log.info("[{}]项目周报，All-bug等级分布 执行完成", vo.getProjectShortName());
        return i;
    }

    private void sendFeishu(WeeklyProjectVo vo) throws IOException {
        String secret = FeiShuApi.getSecret(feishuProperties.getAppId(), feishuProperties.getAppSecret());
        String weeklyReportUrl = vo.getWeeklyReportUrl();
        String fileName = vo.getFileName();
        JSONObject con = new JSONObject();
        JSONObject all = new JSONObject();
        con.put("zh_cn", all);
        all.put("title", "[" + fileName + "] 周报已更新");
        JSONArray contentJsonArray = new JSONArray();
        all.put("content", contentJsonArray);
        JSONArray subContentJsonArray = new JSONArray();
        JSONObject subject = new JSONObject();
        subject.put("tag", "text");
        subject.put("text", "\r\n点击查看周报==》》 ");
        subContentJsonArray.add(subject);
        JSONObject a = new JSONObject();
        a.put("tag", "a");
        a.put("href", weeklyReportUrl);
        a.put("text", fileName);
        subContentJsonArray.add(a);
        contentJsonArray.add(subContentJsonArray);

        SendVo sendVo = new SendVo();
        sendVo.setMemberId(vo.getPmOu());
        sendVo.setContent(con.toJSONString());
        FeiShuApi.sendPost(sendVo, secret, new StringBuilder());
        log.info("[{}]-周报更新已通知PM", fileName);
    }

    /**
     * 公共更新块
     *
     * @param vo
     * @param file
     * @param requests
     */
    private void addRequests(WeeklyProjectVo vo, File file, List<UpdateBlockRequest> requests) {
        if (file == null) {
            return;
        }
        String name = file.getName();
        String blockId = vo.getBlockId();
        // 通过飞书上传图片至对应的block_id下
        FeishuUploadImageVo imageVo = new FeishuUploadImageVo();
        imageVo.setParentType("docx_image");
        imageVo.setFile(file);
        imageVo.setFileName(name);
        imageVo.setSize(file.length());
        imageVo.setAppSecret(vo.getAppSecret());
        imageVo.setAppId(vo.getAppId());
        imageVo.setParentNode(blockId);
        String fileToken = BaseFeishuJavaUtils.upload(imageVo);
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
        if ("logo.jpg".equals(name)) {
            return;
        }
        file.delete();
    }

    /**
     * 替换图片
     *
     * @param vo
     */
    private void replaceImaage(WeeklyProjectVo vo, File file) throws Exception {
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
        String fileToken = BaseFeishu.cloud(client).uploadFile(imageVo);
        vo.setImageToken(fileToken);
        // 通过飞书替换图片至block_id
        Boolean updateBlocks = BaseFeishu.block(client).updateBlocks(vo);
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

    @Deprecated
    public WeeklyVo resetReport(WeeklyVo vo) throws Exception {
        File logoFile = new File(ResourceUtils.path());
        String projectShortName = vo.getProjectShortName();
        vo.setAppSecret(feishuProperties.getAppSecret());
        vo.setAppId(feishuProperties.getAppId());
        String title = vo.getTitle();
        List<Issue> allBugList = WeeklyProjectRedmineUtils.OverallBug.allBug(vo);
        vo.setAllBugList(allBugList);

        JSONArray jsonArray = WeeklyProjectFeishuUtils.blocks(vo);
        ArrayList<UpdateBlockRequest> requests = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(jsonArray).size(); i++) {
            JSONObject block = JSONObject.parseObject(jsonArray.get(i).toString());
            Integer blockType = block.getInteger("block_type");
            if (4 == blockType) {
                String heading2 = JSONObject.parseObject(block.getJSONObject("heading2").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                if (WeeklyReportConstants.REVIEW_QUESTIONS.equals(heading2) && WeeklyReportConstants.REVIEW_QUESTIONS.equals(title)) {
                    reviewQuestions(vo, jsonArray, requests, i);
                    break;
                }
                if (WeeklyReportConstants.COPQ.equals(heading2) && WeeklyReportConstants.COPQ.equals(title)) {
                    copq(vo, jsonArray, requests, i);
                    break;
                }
            }
            if (5 == blockType) {
                String heading3 = JSONObject.parseObject(block.getJSONObject("heading3").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                if (WeeklyReportConstants.TRENDS.equals(heading3) && WeeklyReportConstants.TRENDS.equals(title)) {
                    tends(vo, jsonArray, requests, i);
                    break;
                }
                if (WeeklyReportConstants.BUG_LEVEL.equals(heading3)) {
                    if ("All-Bug等级分布".equals(title)) {
                        allBugLevel(vo, jsonArray, requests, i);
                        break;
                    }
                    if ("Open-Bug等级分布".equals(title)) {
                        openBugLevel(vo, jsonArray, requests, i);
                        break;
                    }
                    if ("Open-Bug>15".equals(title)) {
                        openBug15(logoFile, vo, jsonArray, requests, i);
                        break;
                    }
                }
            }
        }
        BaseFeishu.block(client).batchUpdateBlocks(vo, requests.toArray(new UpdateBlockRequest[0]));
//        BaseFeishuJavaUtils.batchUpdateBlocks(vo, requests.toArray(new UpdateBlockRequest[0]));
        log.info("[{}]项目周报更新完成", projectShortName);
        return vo;
    }

    public int copq(WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
        vo.setBlockId(block.getString("block_id"));
        // 评审问题
        File file = WeeklyProjectUtils.copq(vo);
        addRequests(vo, file, requests);
        log.info("{}项目周报，COPQ 执行完成", vo.getProjectShortName());
        return i;
    }

    /**
     * 趋势
     *
     * @param weeklyVo
     * @param jsonArray
     * @param requests
     * @param i
     * @return
     */
    public int tends(WeeklyProjectVo weeklyVo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i) throws Exception {
        JSONObject block;
        block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
        // 趋势
        weeklyVo.setBlockId(block.getString("block_id"));
        File file = WeeklyProjectUtils.trends(weeklyVo);
        addRequests(weeklyVo, file, requests);
        log.info("[{}]项目周报，趋势 执行完成", weeklyVo.getProjectShortName());
        return i;
    }
}
