package com.q.reminder.reminder;

import com.lark.oapi.Client;
import com.lark.oapi.core.cache.LocalCache;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.drive.v1.model.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.test.TestFeishu
 * @Description :
 * @date :  2023.04.10 11:33
 */
public class TestFeishu {
    private static final String appId = "cli_a1144b112738d013";
    private static final String appSecret = "AQHvpoTxE4pxjkIlcOwC1bEMoJMkJiTx";
    private static final String filter = "D:\\work\\工作记录\\.个人\\项目管理\\福特\\管理文档\\周报\\美行周报Phase3Batch2\\";
    private static final String folderToken = "fldcnchSX6eN2w7cmfnHlqnAzie";
    // 构建client
    private final static Client client = Client.newBuilder(appId, appSecret).logReqAtDebug(true)
            .requestTimeout(5, TimeUnit.MINUTES)
            .tokenCache(LocalCache.getInstance())
            .openBaseUrl(BaseUrlEnum.FeiShu)
            .tokenCache(LocalCache.getInstance())
            .build();



//    public static void main(String arg[]) throws Exception {
//        List<File> listFile = getFiles();
//        for (File file : listFile) {
//            String token = file.getToken();
//            String type = file.getType();
//            // 创建导出任务
//            CreateExportTaskResp ticketResp = createTicket(token, type);
//            CreateExportTaskRespBody ticketData = ticketResp.getData();
//            String ticket = ticketData.getTicket();
//
//            // 查询导出任务结果
//            String fileToken = checkStatus(ticket, token);
//            while (StringUtils.isBlank(fileToken)) {
//                fileToken = checkStatus(ticket, token);
//                Thread.sleep(5000);
//                System.err.println("-------------------------------休息5秒-------------------------------");
//            }
//
//            if (StringUtils.isNotBlank(fileToken)) {
//                downFile(fileToken, type);
//            }
//        }
//        System.err.println("===============================SUCCESS===============================");
//        System.exit(0);
//    }

    private static void downFile(String fileToken, String type) throws Exception {
        // 下载文件
        DownloadExportTaskReq req = DownloadExportTaskReq.newBuilder()
                .fileToken(fileToken)
                .build();
        // 发起请求
        try {
            DownloadExportTaskResp respDown = client.drive().exportTask().download(req);
            respDown.writeFile(filter + respDown.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteFileByToken(fileToken, type);
    }

    private static void deleteFileByToken(String fileToken, String type) throws Exception {
        // 创建请求对象
        DeleteFileReq req = DeleteFileReq.newBuilder()
                .fileToken(fileToken)
                .type(type)
                .build();

        // 发起请求
        // 如开启了Sdk的token管理功能，就无需调用 RequestOptions.newBuilder().tenantAccessToken("t-xxx").build()来设置租户token了
        DeleteFileResp resp = client.drive().file().delete(req, RequestOptions.newBuilder()
                .build());
        if (resp.success()) {
            System.err.println(resp.getData().getTaskId() + "删除完成");
        }
    }

    private static String checkStatus(String ticket, String token) throws Exception {
        // 创建请求对象
        GetExportTaskReq req = GetExportTaskReq.newBuilder()
                .ticket(ticket)
                .token(token)
                .build();

        // 发起请求
        // 如开启了Sdk的token管理功能，就无需调用 RequestOptions.newBuilder().tenantAccessToken("t-xxx").build()来设置租户token了
        GetExportTaskResp resp = client.drive().exportTask().get(req, RequestOptions.newBuilder()
                .build());
        ExportTask result = resp.getData().getResult();
        return result.getFileToken();
    }

    @NotNull
    private static CreateExportTaskResp createTicket(String token, String type) throws Exception {
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
        return client.drive().exportTask().create(req, RequestOptions.newBuilder().build());
    }

    @NotNull
    private static List<File> getFiles() throws Exception {
        ListFileRespBody respBody = getFileList(null);
        List<File> listFile = new ArrayList<>();
        listFile.addAll(List.of(respBody.getFiles()));
        while (respBody.getHasMore()) {
            String nextPageToken = respBody.getNextPageToken();
            if (StringUtils.isNotBlank(nextPageToken)) {
                respBody = getFileList(nextPageToken);
                listFile.addAll(List.of(respBody.getFiles()));
            } else {
                break;
            }
        }
        return listFile;
    }

    public static ListFileRespBody getFileList(String pageToken) throws Exception {
        ListFileReq.Builder fileBuilder = ListFileReq.newBuilder().folderToken(folderToken);
        if (StringUtils.isNotBlank(pageToken)) {
            fileBuilder.pageToken(pageToken);
        }
        // 创建请求对象

        ListFileReq req = fileBuilder.build();
        // 发起请求
        // 如开启了Sdk的token管理功能，就无需调用 RequestOptions.newBuilder().tenantAccessToken("t-xxx").build()来设置租户token了
        ListFileResp resp = client.drive().file().list(req);
        return resp.getData();
    }

}
