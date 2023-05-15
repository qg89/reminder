package com.q.reminder.reminder.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import tech.powerjob.worker.log.OmsLogger;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.WraterExcelSendFeishuUtil
 * @Description :
 * @date :  2022.11.16 13:15
 */
@Log4j2
public class WraterExcelSendFeishuUtil {

    public static void wraterExcelSendFeishu(Map<String, List<ExcelVo>> map, WeeklyProjectVo weeklyVo, String name, WeeklyLogVo<Logger, OmsLogger> objLog) throws Exception {
        OmsLogger omsLogger = objLog.getOmsLogger();
        String path = WeeklyProjectUtils.createDir() + weeklyVo.getFileName() + "-" + name + ".xls";
        String pmName = weeklyVo.getPmName();
        File file = new File(path);
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.renameSheet(0, map.keySet().stream().findFirst().get());
        map.forEach((k, excelVoList) -> {
            writer.setSheet(k);
            writer.writeCellValue(0, 0, "主题");
            writer.writeCellValue(1, 0, "XX年XX周");
            writer.writeCellValue(2, 0, "状态");
            writer.writeCellValue(3, 0, "指派给");
            writer.writeCellValue(4, 0, "内容描述");
            writer.writeCellValue(5, 0, "创建时间");
            writer.writeCellValue(6, 0, "计划完成时间");
            writer.passRows(1);
            writer.setFreezePane(1);
            writer.write(excelVoList, false);
        });
        writer.setDestFile(file);
        writer.close();

        FeishuUploadImageVo vo = new FeishuUploadImageVo();
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreCase(true);
        BeanUtil.copyProperties(weeklyVo, vo, copyOptions);
        ContentVo contentVo = new ContentVo();
        contentVo.setFileType("xls");
        contentVo.setReceiveId(vo.getPmOu());
        contentVo.setFile(file);
        contentVo.setReceiveId(weeklyVo.getPmOu());
        String fileKey = BaseFeishu.message().imUploadFile(contentVo);
        if (StringUtils.isBlank(fileKey)) {
            if (omsLogger != null) {
                omsLogger.info("周报导出Excel,上传文件获取key为空");
            } else {
                log.info("周报导出Excel,上传文件获取key为空");
            }
            return;
        }
        JSONObject json = new JSONObject();
        json.put("file_key", fileKey);
        contentVo.setContent(json.toJSONString());
        contentVo.setMsgType("file");
        contentVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
        CreateMessageResp resp;
        try {
            resp = BaseFeishu.message().sendContent(contentVo);
        } finally {
            file.delete();
        }
        boolean success = resp.success();
        if (!success) {
            String msg = "周报导出Excel文件, 发送给项目经理: 【{}】, error msg : {}  ,  error : {} ";
            if (omsLogger != null) {
                omsLogger.info(msg, pmName, resp.getMsg(), resp.getError());
            } else {
                log.info(msg, pmName, resp.getMsg(), resp.getError());
            }
            return;
        }
        String msg = "周报导出Excel文件, 发送给项目经理: 【{}】, done ！";
        if (omsLogger != null) {
            omsLogger.info(msg, pmName);
        } else {
            log.info(msg, pmName);
        }

    }

    public static void wraterExcelTimeSendFeishu(Map<String, List<ExcelVo.ExcelTimeVo>> map, WeeklyProjectVo weeklyVo, String name) throws Exception {
        String path = WeeklyProjectUtils.createDir() + weeklyVo.getFileName() + "-" + name + ".xls";
        File file = new File(path);
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.renameSheet(0, map.keySet().stream().findFirst().get());

        map.forEach((k, excelVoList) -> {
            writer.setSheet(k);
            writer.writeCellValue(0, 0, "周数");
            writer.writeCellValue(1, 0, "总工时");
            writer.writeCellValue(2, 0, "BUG工时");
            writer.writeCellValue(3, 0, "8月1日总工时");
            writer.writeCellValue(4, 0, "8月1日BUG工时");
            writer.passRows(1);
            writer.setFreezePane(1);
            writer.write(excelVoList, false);
        });

        writer.setDestFile(file);
        writer.close();

        FeishuUploadImageVo vo = new FeishuUploadImageVo();
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreCase(true);
        BeanUtil.copyProperties(weeklyVo, vo, copyOptions);
        ContentVo contentVo = new ContentVo();
        contentVo.setFileType("xls");
        contentVo.setReceiveId(vo.getPmOu());
        contentVo.setFile(file);
        contentVo.setReceiveId(weeklyVo.getPmOu());
        String fileKey;
        try {
            fileKey = BaseFeishu.message().imUploadFile(contentVo);
        } finally {
            file.delete();
        }
        if (StringUtils.isBlank(fileKey)) {
            log.info("周报导出Excel,上传文件获取key为空");
            return;
        }
        JSONObject json = new JSONObject();
        json.put("file_key", fileKey);
        contentVo.setContent(json.toJSONString());
        contentVo.setMsgType("file");
        contentVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
        CreateMessageResp resp = BaseFeishu.message().sendContent(contentVo);
        boolean success = resp.success();
        if (!success) {
            log.info("周报导出Excel, 发送给: {}, error msg : {} , error: {}！", name, resp.getMsg(), resp.getError());
        }
        log.info("周报导出Excel, 发送给: {}, done ！", name);
    }
}
