package com.q.reminder.reminder.task.me;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.vo.RedmineNoneTimeVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.RedmineTimeTask
 * @Description :
 * @date :  2023.11.14 13:54
 */
@AllArgsConstructor
@Component
public class RedmineTimeTask implements BasicProcessor {

    private final PowerJobClient client;
    private final RdTimeEntryService rdTimeEntryService;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult result = new ProcessResult(true);
        String dateTime = DateUtil.beginOfMonth(new Date()).toString("yyyy-MM-dd");
        String yesterday = DateUtil.yesterday().toString("yyyy-MM-dd");
        try {
            List<RedmineNoneTimeVo> list = rdTimeEntryService.listNoneTimeUsers(dateTime, yesterday);
            Map<Integer, List<RedmineNoneTimeVo>> userMap = list.stream().collect(Collectors.groupingBy(RedmineNoneTimeVo::getUserId));
            sendFeishu(userMap, log);
        }catch (Exception e){
            result.setSuccess(false);
            ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
            String taskName = resultDTO.getData().getJobName();
            throw new FeishuException(e, taskName + "- 异常");
        }
        return result;
    }

    private void sendFeishu(Map<Integer, List<RedmineNoneTimeVo>> map, OmsLogger log) {
        JSONObject con = new JSONObject();
        JSONObject all = new JSONObject();
        con.put("zh_cn", all);
        all.put("title", "周报已更新");
        JSONArray contentJsonArray = new JSONArray();
        all.put("content", contentJsonArray);
        JSONArray subContentJsonArray = new JSONArray();
        JSONObject subject = new JSONObject();
        subject.put("tag", "text");
        subject.put("text", "\r\n点击查看周报==》》 ");
        subContentJsonArray.add(subject);
        contentJsonArray.add(subContentJsonArray);

//        MessageVo vo = new MessageVo();
//        vo.setReceiveId(FeiShuContents.ADMIN_MEMBERS);
//        vo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
//        vo.setContent(con.toJSONString());
//        vo.setMsgType("post");
//        CreateMessageResp resp = BaseFeishu.message().sendContent(vo);
//        log.info("返回飞书报文，{}", resp);
        log.info("未填日报集合。{}", map);
    }
}
