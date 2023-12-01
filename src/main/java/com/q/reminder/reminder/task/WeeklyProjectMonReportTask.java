package com.q.reminder.reminder.task;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.docx.v1.model.ReplaceImageRequest;
import com.lark.oapi.service.docx.v1.model.UpdateBlockRequest;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.constant.WeeklyReportConstants;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.util.HolidayUtils;
import com.q.reminder.reminder.util.ResourceUtils;
import com.q.reminder.reminder.util.WeeklyProjectRedmineUtils;
import com.q.reminder.reminder.util.WeeklyProjectUtils;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;
import com.q.reminder.reminder.vo.MessageVo;
import com.q.reminder.reminder.vo.WeeklyLogVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

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
 * @ClassName : com.q.reminder.reminder.handle.WeeklyProjectMonReportHandle
 * @Description : 周一中午12点 写日报
 * @date :  2022.11.01 17:55
 */
@Deprecated
@Log4j2
@Component
@RequiredArgsConstructor
public class WeeklyProjectMonReportTask implements BasicProcessor {
    private final ProjectInfoService projectInfoService;
    private final FeishuProperties feishuProperties;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        ProcessResult result = new ProcessResult(true);
        if (HolidayUtils.isHoliday()) {
            log.info(taskName + "-放假咯");
            return result;
        }
        try {
            log.info(taskName + "-start");
            String instanceParams = context.getInstanceParams();
            int weekNumber = 0;
            String id = null;
            if (StringUtils.isNotBlank(instanceParams)) {
                String[] param = instanceParams.split(",");
                String weekNum = param[0];
                if (StringUtils.isNotBlank(weekNum) && NumberUtil.isInteger(weekNum)) {
                    weekNumber = Integer.parseInt(weekNum);
                }
                if (param.length > 1 && StringUtils.isNotBlank(param[1])) {
                    id = param[1];
                }
            }
            List<WeeklyProjectVo> list = projectInfoService.getWeeklyDocxList(weekNumber, id);
            log.info(taskName + "-start write");
            this.writeReport(list, log);
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "-异常");
        }
        log.info(taskName + "-done");
        return result;
    }

    private void writeReport(List<WeeklyProjectVo> list, OmsLogger log) throws Exception {
        WeeklyLogVo<Logger, OmsLogger> logVo = new WeeklyLogVo<>(log);
        File logoFile = new File(ResourceUtils.path());
        for (WeeklyProjectVo report : list) {
            Date sunday = getWeekNumToSunday(report.getWeekNum() - 1);
            Date startDay = report.getStartDay();
            report.setAppId(feishuProperties.getAppId());
            report.setAppSecret(feishuProperties.getAppSecret());
            List<Issue> allBugList = WeeklyProjectRedmineUtils.OverallBug.allBug(report).stream().filter(e -> {
                if (startDay == null) {
                    return true;
                } else {
                    return e.getCreatedOn().after(startDay) && e.getCreatedOn().before(sunday);
                }
            }).collect(Collectors.toList());
            report.setAllBugList(allBugList);

            JSONArray jsonArray = BaseFeishu.cloud().documents().blocks(report);
            ArrayList<UpdateBlockRequest> requests = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(jsonArray).size(); i++) {
                JSONObject block = JSONObject.parseObject(jsonArray.get(i).toString());
                Integer blockType = block.getInteger("block_type");
                if (4 == blockType) {
                    String heading2 = JSONObject.parseObject(block.getJSONObject("heading2").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                    if (WeeklyReportConstants.REVIEW_QUESTIONS.equals(heading2)) {
                        i = reviewQuestions(report, jsonArray, requests, i, logVo);
                    }
                    if (WeeklyReportConstants.COPQ.equals(heading2)) {
                        i = copq(report, jsonArray, requests, i, logVo);
                    }
                }
                if (5 == blockType) {
                    String heading3 = JSONObject.parseObject(block.getJSONObject("heading3").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                    if (WeeklyReportConstants.TRENDS.equals(heading3)) {
                        i = tends(report, jsonArray, requests, i, logVo);
                    }
                    if (WeeklyReportConstants.BUG_LEVEL.equals(heading3)) {
                        i = allBugLevel(report, jsonArray, requests, i, logVo);
                        i = openBugLevel(report, jsonArray, requests, i, logVo);
                        i = openBug15(logoFile, report, jsonArray, requests, i, logVo);
                    }
                }
            }
            BaseFeishu.cloud().documents().batchUpdateBlocks(report, requests.toArray(new UpdateBlockRequest[0]));
            log.info("[{}]项目周报更新完成", report.getProjectShortName());
            sendFeishu(report);
            log.info("[{}]项目周报飞书发送完成", report.getProjectShortName());
        }
    }

    /**
     * 评审问题
     *
     * @param vo
     * @param jsonArray
     * @param requests
     * @param i
     * @param objLog
     * @return
     */
    public int reviewQuestions(WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i, WeeklyLogVo<Logger, OmsLogger> objLog) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
        String projectShortName = vo.getProjectShortName();
        vo.setBlockId(block.getString("block_id"));
        // 评审问题
        File file = WeeklyProjectUtils.reviewQuestions(vo, objLog);
        addRequests(vo, file, requests);
        OmsLogger omsLogger = objLog.getOmsLogger();
        if (omsLogger != null) {
            omsLogger.info("[{}]项目周报，评审问题 执行完成", projectShortName);
        } else {
            log.info("[{}]项目周报，评审问题 执行完成", projectShortName);
        }
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
     * @param logVo
     * @return
     */
    public int openBug15(File logoFile, WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i, WeeklyLogVo<Logger, OmsLogger> logVo) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
        String projectShortName = vo.getProjectShortName();
        // open-Bug >15
        vo.setBlockId(block.getString("block_id"));
        File openBug15 = WeeklyProjectUtils.openBug15(vo.getAllBugList(), logVo);
        if (openBug15 == null) {
            openBug15 = logoFile;
        }
        addRequests(vo, openBug15, requests);
        OmsLogger omsLogger = logVo.getOmsLogger();
        if (omsLogger != null) {
            omsLogger.info("[{}]项目周报，open-Bug >15 执行完成", projectShortName);
        } else {
            log.info("[{}]项目周报，open-Bug >15 执行完成", projectShortName);
        }
        return i;
    }

    /**
     * Open-Bug等级分布
     *
     * @param vo
     * @param jsonArray
     * @param requests
     * @param i
     * @param logVo
     * @return
     */
    public int openBugLevel(WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i, WeeklyLogVo<Logger, OmsLogger> logVo) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
        String projectShortName = vo.getProjectShortName();
        // Open-Bug等级
        vo.setBlockId(block.getString("block_id"));
        File openBug = WeeklyProjectUtils.openBug(vo.getAllBugList(), vo, logVo);
        addRequests(vo, openBug, requests);
        OmsLogger omsLogger = logVo.getOmsLogger();
        if (omsLogger != null) {
            omsLogger.info("[{}]项目周报，Open-Bug等级分布 执行完成", projectShortName);
        } else {
            log.info("[{}]项目周报，Open-Bug等级分布 执行完成", projectShortName);
        }
        return i;
    }

    /**
     * All-bug等级分布
     *
     * @param vo
     * @param jsonArray
     * @param requests
     * @param i
     * @param logVo
     * @return
     */
    public int allBugLevel(WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i, WeeklyLogVo<Logger, OmsLogger> logVo) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
        String projectShortName = vo.getProjectShortName();
        // All-bug等级
        vo.setBlockId(block.getString("block_id"));
        File bugLevel = WeeklyProjectUtils.AllBugLevel(vo.getAllBugList(), vo, logVo);
        addRequests(vo, bugLevel, requests);
        OmsLogger omsLogger = logVo.getOmsLogger();
        if (omsLogger != null) {
            omsLogger.info("[{}]项目周报，All-bug等级分布 执行完成", projectShortName);
        } else {
            log.info("[{}]项目周报，All-bug等级分布 执行完成", projectShortName);
        }
        return i;
    }

    private void sendFeishu(WeeklyProjectVo vo) {
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

        MessageVo sendVo = new MessageVo();
        sendVo.setReceiveId(vo.getPmOu());
        sendVo.setContent(con.toJSONString());
        sendVo.setMsgType("post");
        sendVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
        BaseFeishu.message().sendContent(sendVo);
    }

    /**
     * 公共更新块
     *
     * @param vo
     * @param file
     * @param requests
     */
    private void addRequests(WeeklyProjectVo vo, File file, List<UpdateBlockRequest> requests) throws Exception {
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
        String fileToken = BaseFeishu.cloud().upload().uploadFile(imageVo);
        if (StringUtils.isBlank(fileToken)) {
//            log.info("飞书上传素材返回为空");
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
        String fileToken = BaseFeishu.cloud().upload().uploadFile(imageVo);
        vo.setImageToken(fileToken);
        // 通过飞书替换图片至block_id
        Boolean updateBlocks = BaseFeishu.cloud().documents().updateBlocks(vo);
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

    public int copq(WeeklyProjectVo vo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i, WeeklyLogVo<Logger, OmsLogger> logVo) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 2))).toString());
        String projectShortName = vo.getProjectShortName();
        vo.setBlockId(block.getString("block_id"));
        // 评审问题
        File file = WeeklyProjectUtils.copq(vo);
        addRequests(vo, file, requests);
        OmsLogger omsLogger = logVo.getOmsLogger();
        if (omsLogger != null) {
            omsLogger.info("[{}]项目周报，COPQ 执行完成", projectShortName);
        } else {
            log.info("[{}]项目周报，COPQ 执行完成", projectShortName);
        }
        return i;
    }

    /**
     * 趋势
     *
     * @param weeklyVo
     * @param jsonArray
     * @param requests
     * @param i
     * @param logVo
     * @return
     */
    public int tends(WeeklyProjectVo weeklyVo, JSONArray jsonArray, ArrayList<UpdateBlockRequest> requests, int i, WeeklyLogVo<Logger, OmsLogger> logVo) throws Exception {
        JSONObject block = JSONObject.parseObject(jsonArray.get((i = (i + 1))).toString());
        String projectShortName = weeklyVo.getProjectShortName();
        // 趋势
        weeklyVo.setBlockId(block.getString("block_id"));
        File file = WeeklyProjectUtils.trends(weeklyVo, logVo);
        addRequests(weeklyVo, file, requests);
        OmsLogger omsLogger = logVo.getOmsLogger();
        if (omsLogger != null) {
            omsLogger.info("[{}]项目周报，趋势 执行完成", projectShortName);
        } else {
            log.info("[{}]项目周报，趋势 执行完成", projectShortName);
        }
        return i;
    }
}
