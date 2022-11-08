package com.q.reminder.reminder.handle;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.docx.v1.model.ReplaceImageRequest;
import com.lark.oapi.service.docx.v1.model.UpdateBlockRequest;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.contents.WeeklyReportContents;
import com.q.reminder.reminder.handle.base.HoldayBase;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.util.FeishuJavaUtils;
import com.q.reminder.reminder.util.WeeklyProjectFeishuUtils;
import com.q.reminder.reminder.util.WeeklyProjectUtils;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
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

            JSONArray jsonArray = WeeklyProjectFeishuUtils.blocks(vo);
            ArrayList<UpdateBlockRequest> requests = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(jsonArray).size(); i++) {
                JSONObject block = JSONObject.parseObject(jsonArray.get(i).toString());
                Integer blockType = block.getInteger("block_type");
                if (4 == blockType) {
                    String pswt = JSONObject.parseObject(block.getJSONObject("heading2").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                    if (WeeklyReportContents.REVIEW_QUESTIONS.equals(pswt)) {
                        block = JSONObject.parseObject(jsonArray.get(i + 2).toString());
                        vo.setBlockId(block.getString("block_id"));
                        // 评审问题
                        File file = WeeklyProjectUtils.reviewQuestions(vo);
//                        replaceImaage(vo, file);
                        addRequests(vo, file, requests);
                        if (file.isFile()) {
                            file.delete();
                        }
                        i = i + 2;
                        vo.setBlockId(null);
                    }
                }
                if (5 == blockType) {
                    String qs = JSONObject.parseObject(block.getJSONObject("heading3").getJSONArray("elements").get(0).toString()).getJSONObject("text_run").getString("content");
                    if (WeeklyReportContents.TRENDS.equals(qs)) {
                        block = JSONObject.parseObject(jsonArray.get(i + 1).toString());
                        // 趋势
                        vo.setBlockId(block.getString("block_id"));
                        File file = WeeklyProjectUtils.trends(vo);
//                        replaceImaage(vo, file);
                        addRequests(vo, file, requests);
                        if (file.isFile()) {
                            file.delete();
                        }
                        i = i + 1;
                        vo.setBlockId(null);
                    }
                }
            }
            FeishuJavaUtils.batchUpdateBlocks(vo, requests.toArray(new UpdateBlockRequest[0]));
        });
    }

    private void addRequests(WeeklyProjectVo vo, File file, List<UpdateBlockRequest> requests) {
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
        UpdateBlockRequest update = UpdateBlockRequest.newBuilder().build();
        update.setReplaceImage(ReplaceImageRequest.newBuilder()
                .token(fileToken)
                .build());
        update.setBlockId(vo.getBlockId());
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
