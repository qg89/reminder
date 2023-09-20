package com.q.reminder.reminder.util.feishu.cloud.documents;

import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.drive.v1.model.*;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.cloud.documents.Download
 * @Description :
 * @date :  2023.09.19 15:31
 */
public class ExportFile extends BaseFeishu {

    private static ExportFile instance;

    private ExportFile() {
        super();
    }

    public static synchronized ExportFile getInstance() {
        if (instance == null) {
            instance = new ExportFile();
        }
        return instance;
    }

    public void exportFile(String path, String token, String type) throws Exception {
        // 创建导出任务
        CreateExportTaskResp ticketResp = createTicket(token, type);
        CreateExportTaskRespBody ticketData = ticketResp.getData();
        String ticket = ticketData.getTicket();
        // 查询导出任务结果
        String fileToken = BaseFeishu.cloud().export().checkStatus(ticket, token);
        while (StringUtils.isBlank(fileToken)) {
            fileToken = BaseFeishu.cloud().export().checkStatus(ticket, token);
            Thread.sleep(5000);
            System.err.println("-------------------------------休息5秒-------------------------------");
        }
        downFile(path, fileToken);
    }

    /**
     * 导出文件
     *
     * @param path
     * @param fileToken
     * @throws Exception
     */
    private void downFile(String path, String fileToken) throws Exception {
        // 下载文件
        DownloadExportTaskReq req = DownloadExportTaskReq.newBuilder()
                .fileToken(fileToken)
                .build();
        // 发起请求
        try {
            DownloadExportTaskResp respDown = CLIENT.drive().exportTask().download(req);
            respDown.writeFile(path + respDown.getFileName());
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 下载文件异常");
        }
    }

    /**
     * 检查下载任务状态
     *
     * @param ticket
     * @param token
     * @return
     */
    private String checkStatus(String ticket, String token) {
        // 创建请求对象
        GetExportTaskReq req = GetExportTaskReq.newBuilder()
                .ticket(ticket)
                .token(token)
                .build();

        // 发起请求
        GetExportTaskResp resp = null;
        try {
            resp = CLIENT.drive().exportTask().get(req, RequestOptions.newBuilder().build());
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 获取下载文件状态异常");
        }
        ExportTask result = resp.getData().getResult();
        return result.getFileToken();
    }

    /**
     * 创建下载任务
     *
     * @param token
     * @param type
     * @return
     */
    @NotNull
    private CreateExportTaskResp createTicket(String token, String type) {
        // 创建请求对象
        CreateExportTaskReq req = CreateExportTaskReq.newBuilder()
                .exportTask(ExportTask.newBuilder()
                        .fileExtension("docx")
                        .token(token)
                        .type(type)
                        .build())
                .build();

        // 发起请求
        // 如开启了Sdk的token管理功能，就无需调用 RequestOptions.newBuilder().tenantAccessToken("t-xxx").build()来设置租户token了
        try {
            return CLIENT.drive().exportTask().create(req, RequestOptions.newBuilder().build());
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 创建下载文件状态异常");
        }
    }

    public void deleteFileByToken(String fileToken, String type) {
        // 创建请求对象
        DeleteFileReq req = DeleteFileReq.newBuilder()
                .fileToken(fileToken)
                .type(type)
                .build();

        // 发起请求
        // 如开启了Sdk的token管理功能，就无需调用 RequestOptions.newBuilder().tenantAccessToken("t-xxx").build()来设置租户token了
        try {
            DeleteFileResp resp = CLIENT.drive().file().delete(req, RequestOptions.newBuilder()
                    .build());
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 删除源文件异常");
        }
    }
}
