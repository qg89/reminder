package com.q.reminder.reminder.util.feishu.cloud.space;

import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetReq;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetResp;
import com.lark.oapi.service.sheets.v3.model.Sheet;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.SheetVo;

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

    /**
     * 获取电子表格sheets
     *
     * @param spreadsheetToken
     * @return
     */
    public List<SheetVo> getSpredsheets(String spreadsheetToken) throws Exception {
        // 创建请求对象
        QuerySpreadsheetSheetReq req = QuerySpreadsheetSheetReq.newBuilder()
                .spreadsheetToken(spreadsheetToken)
                .build();
        // 发起请求
        QuerySpreadsheetSheetResp resp = CLIENT.sheets().spreadsheetSheet().query(req, RequestOptions.newBuilder().tenantAccessToken(TENANT_ACCESS_TOKEN).build());
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
