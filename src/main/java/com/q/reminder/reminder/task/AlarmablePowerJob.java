package com.q.reminder.reminder.task;

import com.lark.oapi.Client;
import com.lark.oapi.core.Config;
import com.lark.oapi.core.enums.AppType;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.lark.oapi.core.httpclient.OkHttpTransport;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.OKHttps;
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldReq;
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldResp;
import com.q.reminder.reminder.cpp.Demo123;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.SHolidayConfigService;
import com.q.reminder.reminder.service.TableFieldsChangeService;
import com.q.reminder.reminder.service.impl.FeishuService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.concurrent.TimeUnit;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.AlarmablePowerJob
 * @Description :
 * @date :  2023.03.27 18:59
 */
@RequiredArgsConstructor
@Component
public class AlarmablePowerJob implements BasicProcessor {
    private final PowerJobClient client;
    private final SHolidayConfigService service;
    private final TableFieldsChangeService tableFieldsChangeService;
    private final FeishuService feishuService;
    private final ProjectInfoService projectInfoService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
//        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
//        String taskName = resultDTO.getData().getJobName();
//
        ProcessResult processResult = new ProcessResult(true);
        OmsLogger omsLogger = context.getOmsLogger();
        omsLogger.info("init - system loadLibary");
        System.loadLibrary("Demo123");
        omsLogger.info("init - so success");
        Demo123 demo123 = new Demo123();
        String i = demo123.sayHello("111");
        omsLogger.info("报文信息:" , i);

//        Boolean test = RedisUtils.getInstance().invokeExceededTimes("test", 10, 10);
//
//        List<FsGroupInfo> groupToChats = BaseFeishu.groupMessage().getGroupToChats();
//        System.out.println();
//        DateTime now = new DateTime(context.getInstanceParams()).minusDays(1);
//        boolean flag = true;
//        List<SHolidayConfig> data = new ArrayList<>();
//        while (flag) {
//            now = now.plusDays(1);
//            Date nowDate = now.toDate();
//            Holiday holiday = HolidayUtil.getHoliday(now.toString("yyyy-MM-dd"));
//            SHolidayConfig sHolidayConfig = new SHolidayConfig();
//            if ((holiday == null && !DateUtil.isWeekend(nowDate)) || holiday != null && holiday.isWork()) {
//                sHolidayConfig.setDate(nowDate);
//                sHolidayConfig.setWork(1);
//                if (holiday != null) {
//                    sHolidayConfig.setName(holiday.toString());
//                }
//            } else {
//                sHolidayConfig.setWork(0);
//                sHolidayConfig.setDate(nowDate);
//                if (holiday != null) {
//                    sHolidayConfig.setName(holiday.getName());
//                }
//            }
//            data.add(sHolidayConfig);
//            if (DateUtil.isLastDayOfMonth(nowDate)) {
//                flag = false;
//            }
//        }
//        service.saveOrUpdateBatch(data);
//        List<TableFieldsChange> list = new ArrayList<>();
//        String token = "";
//        ListAppTableFieldRespBody data = chan(token).getData();
//        List<AppTableField> li = new ArrayList<>(List.of(data.getItems()));
//        while (data.getHasMore()) {
//            token = data.getPageToken();
//            data = chan(token).getData();
//            li.addAll(List.of(data.getItems()));
//        }
//        for (AppTableField file : li) {
//            TableFieldsChange tmp = new TableFieldsChange();
//            tmp.setFieldId(file.getFieldId());
//            tmp.setType(file.getType());
//            tmp.setFieldName(file.getFieldName());
////            tmp.setProperty(JSONObject.from(file.getProperty() == null ? "" : file.getProperty()));
//            tmp.setFileToken("bascnrkdLGoUftLgM7fvME7ly5c");
//            tmp.setTableId("tbld61CFebNfZ6M6");
//            list.add(tmp);
//        }
//        tableFieldsChangeService.saveOrUpdateBatchByMultiId(list);


//        List<RProjectInfo> projectInfoList = projectInfoService.listAll().stream().filter(e -> StringUtils.isNotBlank(e.getPmKey())).toList();
//        List<RedmineVo> issues = RedmineApi.queryUpdateIssue(projectInfoList);
//        String remoteRepoPath = "ssh://git@192.168.3.40:1022/mx-4s/telematics/telematics_adp.git";
//        String keyPath = "/id_rsa_46";
//        String localPath = "/test123";
//        JGitUtils.gitClone(remoteRepoPath, localPath, keyPath);
//        List<GitCount> master = JGitUtils.commitResolver(localPath, "master");
//        OmsLogger omsLogger = context.getOmsLogger();
//        omsLogger.info("master,{}", master);
        return processResult;
    }

    public ListAppTableFieldResp chan(String token) throws Exception {
        // 创建请求对象
        ListAppTableFieldReq req = ListAppTableFieldReq.newBuilder()
                .appToken("bascnrkdLGoUftLgM7fvME7ly5c")
                .tableId("tbld61CFebNfZ6M6")
                .build();
        if (StringUtils.isNotBlank(token)) {
            req.setPageToken(token);
        }
        RequestOptions options = RequestOptions.newBuilder().appAccessToken("u-d6Op03N25a5GCZg.7ht65Dhk4yLA00rxO0w0g0k20bw9").build();
        Config config = new Config();
        config.setBaseUrl(BaseUrlEnum.FeiShu.getUrl());
        config.setAppType(AppType.SELF_BUILT);
        config.setDisableTokenCache(false);
//        config.setAppId("cli_a1144b112738d013");
//        config.setAppSecret("AQHvpoTxE4pxjkIlcOwC1bEMoJMkJiTx");
        config.setHttpTransport(new OkHttpTransport(OKHttps.create(1, TimeUnit.DAYS)));
        // 发起请求
        Client cliented = feishuService.client();
        cliented.setConfig(config);
        return cliented.bitable().appTableField().list(req, options);
    }
}
