package com.q.reminder.reminder.util.feishu.cloud.space;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.drive.v1.model.CopyFileReq;
import com.lark.oapi.service.drive.v1.model.CopyFileReqBody;
import com.lark.oapi.service.drive.v1.model.CopyFileResp;
import com.lark.oapi.service.drive.v1.model.Property;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetReq;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetResp;
import com.lark.oapi.service.sheets.v3.model.Sheet;
import com.q.reminder.reminder.entity.WeeklyProjectReport;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.SheetVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.cld.space.UploadFile
 * @Description :
 * @date :  2023.02.23 11:23
 */
public class Space extends BaseFeishu {
    private static Space instance;

    private Space() {
        super();
    }

    public static synchronized Space getInstance() {
        if (instance == null) {
            instance = new Space();
        }
        return instance;
    }

    public WeeklyProjectReport copyFile(WeeklyProjectVo vo) {
        String weekNum = String.valueOf(DateUtil.thisWeekOfYear());
        CopyFileReq req = CopyFileReq.newBuilder()
                .fileToken(vo.getFileToken())
                .copyFileReqBody(CopyFileReqBody.newBuilder()
                        .name("【业务三部】" + vo.getProjectShortName() + "项目周报-" + DateTime.now().toString("yy") + "W" + weekNum)
                        .type("docx")
                        .folderToken(vo.getFolderToken())
                        .extra(new Property[]{})
                        .build())
                .build();
        CopyFileResp resp = null;
        try {
            resp = CLIENT.drive().file().copy(req, REQUEST_OPTIONS);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 复制文件异常");
        }
        if (resp.success()) {
            JSONObject result = JSONObject.parseObject(Jsons.DEFAULT.toJson(resp.getData()));
            WeeklyProjectReport file = JSONObject.parseObject(result.getString("file"), WeeklyProjectReport.class);
            if (file == null) {
                return new WeeklyProjectReport();
            }
            file.setWeekNum(weekNum);
            return file;
        }
        return null;
    }

    /**
     * 获取电子表格sheets
     *
     * @param spreadsheetToken
     * @return
     */
    public List<SheetVo> getSpredsheets(String spreadsheetToken) {
        // 创建请求对象
        QuerySpreadsheetSheetReq req = QuerySpreadsheetSheetReq.newBuilder()
                .spreadsheetToken(spreadsheetToken)
                .build();
        // 发起请求
        QuerySpreadsheetSheetResp resp = null;
        try {
            resp = CLIENT.sheets().spreadsheetSheet().query(req, REQUEST_OPTIONS);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 获取电子表格sheets异常");
        }
        List<SheetVo> list = new ArrayList<>();
        Sheet[] sheets = resp.getData().getSheets();
        for (Sheet sheet : sheets) {
            SheetVo vo = new SheetVo();
            vo.setTitle(sheet.getTitle());
            vo.setSheetId(sheet.getSheetId());
            list.add(vo);
        }
        return list;
    }
}
