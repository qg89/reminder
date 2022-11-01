package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.drive.v1.model.CopyFileReq;
import com.lark.oapi.service.drive.v1.model.CopyFileReqBody;
import com.lark.oapi.service.drive.v1.model.CopyFileResp;
import com.lark.oapi.service.drive.v1.model.Property;
import com.q.reminder.reminder.entity.WeeklyProjectReport;
import com.q.reminder.reminder.vo.WeeklyProjectVo;

import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.WeeklyProjectFeishuUtils
 * @Description :
 * @date :  2022.11.01 14:15
 */
public abstract class WeeklyProjectFeishuUtils {

    public static WeeklyProjectReport copyFile(WeeklyProjectVo vo) {
        String weekNum = String.valueOf(DateUtil.thisWeekOfYear() - 1);
        // 构建client
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();

        // 创建请求对象
        CopyFileReq req = CopyFileReq.newBuilder()
                .fileToken(vo.getFileToken())
                .copyFileReqBody(CopyFileReqBody.newBuilder()
                        .name("【业务三部】" + vo.getProjectSshortName() + "项目周报-" + DateUtil.thisYear() + "W" + weekNum)
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
}
