package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.docx.v1.enums.ListDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.model.GetDocumentBlockChildrenReq;
import com.lark.oapi.service.docx.v1.model.GetDocumentBlockChildrenResp;
import com.lark.oapi.service.docx.v1.model.ListDocumentBlockReq;
import com.lark.oapi.service.docx.v1.model.ListDocumentBlockResp;
import com.lark.oapi.service.drive.v1.model.CopyFileReq;
import com.lark.oapi.service.drive.v1.model.CopyFileReqBody;
import com.lark.oapi.service.drive.v1.model.CopyFileResp;
import com.lark.oapi.service.drive.v1.model.Property;
import com.q.reminder.reminder.entity.WeeklyProjectReport;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.WeeklyProjectFeishuUtils
 * @Description :
 * @date :  2022.11.01 14:15
 */
@Log4j2
public abstract class WeeklyProjectFeishuUtils {

    /**
     * 复制文件
     * @param vo
     * @return
     */
    public static WeeklyProjectReport copyFile(WeeklyProjectVo vo) {
        String weekNum = String.valueOf(DateUtil.thisWeekOfYear());
        // 构建client
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();

        // 创建请求对象
        CopyFileReq req = CopyFileReq.newBuilder()
                .fileToken(vo.getFileToken())
                .copyFileReqBody(CopyFileReqBody.newBuilder()
                        .name("【业务三部】" + vo.getProjectShortName() + "项目周报-" + DateTime.now().toString("yy") + "W" + weekNum)
                        .type("docx")
                        .folderToken(vo.getFolderToken())
                        .extra(new Property[]{})
                        .build())
                .build();

        // 发起请求
        CopyFileResp resp = null;
        try {
            resp = client.drive().file().copy(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 处理服务端错误
        if (!resp.success()) {
            System.out.printf("code:%s,msg:%s,reqId:%s%n", resp.getCode(), resp.getMsg(), resp.getRequestId());
            return null;
        }
        // 业务数据处理
        JSONObject result = JSONObject.parseObject(Jsons.DEFAULT.toJson(resp.getData()));
        WeeklyProjectReport file = JSONObject.parseObject(result.getString("file"), WeeklyProjectReport.class);
        if (file == null) {
            return new WeeklyProjectReport();
        }
        file.setWeekNum(weekNum);
        return file;
    }

    /**
     * 获取文档所有块
     * @param vo
     * @return
     */
    public static JSONArray blocks(WeeklyProjectVo vo) {
        // 构建client
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();

        // 创建请求对象
        ListDocumentBlockReq listDocumentBlockReq = ListDocumentBlockReq.newBuilder()
                .documentId(vo.getFileToken())
                .pageSize(500)
                .documentRevisionId(-1)
                .userIdType(ListDocumentBlockUserIdTypeEnum.USER_ID)
                .build();

        // 发起请求
        ListDocumentBlockResp resp = null;
        try {
            resp = client.docx().documentBlock().list(listDocumentBlockReq);
        } catch (Exception e) {
            log.error(e);
        }
        // 业务数据处理
        if (resp != null && resp.getCode() == 0) {
            JSONObject result = JSONObject.parseObject(Jsons.DEFAULT.toJson(resp.getData()));
            return result.getJSONArray("items");
        }
        return new JSONArray();
    }

//    public static void main(String[] args) {
//        WeeklyProjectVo vo = new WeeklyProjectVo();
//        vo.setAppId("cli_a1144b112738d013");
//        vo.setAppSecret("AQHvpoTxE4pxjkIlcOwC1bEMoJMkJiTx");
//        vo.setFileToken("HOefdW3YdomyO3x2GAOcRsZ9nyb");
//        JSONArray blocks = blocks(vo);
//        System.out.println(blocks);
//    }
}
