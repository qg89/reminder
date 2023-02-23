package com.q.reminder.reminder.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.vo.ContentVo;
import com.q.reminder.reminder.vo.ExcelVo;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import lombok.extern.log4j.Log4j2;

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

    public static void wraterExcelSendFeishu(Map<String, List<ExcelVo>> map, WeeklyProjectVo weeklyVo, String name) throws Exception {
        String appId = weeklyVo.getAppId();
        String appSecret = weeklyVo.getAppSecret();
        String path = WeeklyProjectUtils.createDir() + weeklyVo.getFileName() + "-" + name + ".xls";
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
        contentVo.setAppSecret(appSecret);
        contentVo.setAppId(appId);
        contentVo.setFile(file);
        contentVo.setReceiveId(weeklyVo.getPmOu());
        String fileKey = BaseFeishuJavaUtils.imUploadFile(contentVo);
        JSONObject json = new JSONObject();
        json.put("file_key", fileKey);
        contentVo.setContent(json.toJSONString());
        contentVo.setMsgType("file");
        BaseFeishuJavaUtils.sendContent(contentVo);
        file.delete();
    }

    public static void wraterExcelTimeSendFeishu(Map<String, List<ExcelVo.ExcelTimeVo>> map, WeeklyProjectVo weeklyVo, String name) throws Exception {
        String appId = weeklyVo.getAppId();
        String appSecret = weeklyVo.getAppSecret();
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
        contentVo.setAppSecret(appSecret);
        contentVo.setAppId(appId);
        contentVo.setFile(file);
        contentVo.setReceiveId(weeklyVo.getPmOu());
        String fileKey = BaseFeishuJavaUtils.imUploadFile(contentVo);
        JSONObject json = new JSONObject();
        json.put("file_key", fileKey);
        contentVo.setContent(json.toJSONString());
        contentVo.setMsgType("file");
        BaseFeishuJavaUtils.sendContent(contentVo);
        file.delete();
    }
}
