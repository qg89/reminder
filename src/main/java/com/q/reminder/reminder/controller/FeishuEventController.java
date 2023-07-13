package com.q.reminder.reminder.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.core.response.EventResp;
import com.lark.oapi.event.CustomEventHandler;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.service.approval.v4.ApprovalService;
import com.lark.oapi.service.approval.v4.model.ApprovalEvent;
import com.lark.oapi.service.approval.v4.model.P2ApprovalUpdatedV4;
import com.lark.oapi.service.approval.v4.model.P2ApprovalUpdatedV4Data;
import com.lark.oapi.service.drive.v1.DriveService;
import com.lark.oapi.service.drive.v1.model.BitableTableFieldAction;
import com.lark.oapi.service.drive.v1.model.BitableTableFieldActionValue;
import com.lark.oapi.service.drive.v1.model.P2FileBitableFieldChangedV1;
import com.lark.oapi.service.drive.v1.model.P2FileBitableFieldChangedV1Data;
import com.lark.oapi.service.im.v1.ImService;
import com.lark.oapi.service.im.v1.model.*;
import com.q.reminder.reminder.constant.GroupInfoType;
import com.q.reminder.reminder.entity.FsGroupInfo;
import com.q.reminder.reminder.entity.TableFieldsFeature;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.service.TableFieldsFeatureService;
import com.q.reminder.reminder.service.UserMemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.FeishuEventController
 * @Description :
 * @date :  2023.04.10 19:13
 */
@Log4j2
@RestController
public class FeishuEventController {

    @Autowired
    private TableFieldsFeatureService tableFieldsFeatureService;
    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private UserMemberService userMemberService;
    private static String CHAT_ID = null;

    @PostMapping("/event")
    public void event(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        try {
            String bodyStr = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.info("FeishuEvent request:{}", bodyStr);
            EventReq req = new EventReq();
            req.setHeaders(toHeaderMap(request));
            req.setBody(bodyStr.getBytes(StandardCharsets.UTF_8));
            req.setHttpPath(request.getRequestURI());
            FsGroupInfo groupInfo = groupInfoService.getOne(Wrappers.<FsGroupInfo>lambdaQuery().eq(FsGroupInfo::getSendType, GroupInfoType.DEP_GROUP));
            log.info("FeishuEvent project:{}", groupInfo);
            CHAT_ID = groupInfo.getChatId();
            log.info("FeishuEvent CHAT_ID:{}", CHAT_ID);
            EventResp resp = EVENT_DISPATCHER.handle(req);
            log.info("FeishuEvent EventResp:{}", resp);
            write(response, resp);
        } finally {
            CHAT_ID = null;
        }
        log.info("FeishuEvent done~");
    }

    public void write(HttpServletResponse response, EventResp eventResp) throws IOException {
        response.setStatus(eventResp.getStatusCode());
        eventResp.getHeaders().forEach((key, values) -> values.forEach(v -> response.addHeader(key, v)));
        if (eventResp.getBody() != null) {
            response.getWriter().write(new String(eventResp.getBody()));
        }
    }

    private Map<String, List<String>> toHeaderMap(HttpServletRequest req) {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            List<String> values = Collections.list(req.getHeaders(name));
            headers.put(normalizeKey(name), values);
        }
        return headers;
    }

    private String normalizeKey(String name) {
        return name != null ? name.toLowerCase() : null;
    }

    //1. 注册消息处理器
    private final EventDispatcher EVENT_DISPATCHER = EventDispatcher.newBuilder("hW3E10lr0QH7u22Twj0NqcYSoh5hCeCr", "ofkRjv6TQe4q8ATLDyOYNfyHnFrkRIFA")
            // 字段变更
            .onP2FileBitableFieldChangedV1(new DriveService.P2FileBitableFieldChangedV1Handler() {
                @Override
                public void handle(P2FileBitableFieldChangedV1 event) throws Exception {
                    P2FileBitableFieldChangedV1Data eventEvent = event.getEvent();
                    String tableId = eventEvent.getTableId();
                    if (!"bascnrkdLGoUftLgM7fvME7ly5c".equals(eventEvent.getFileToken())) {
                        return;
                    }
                    BitableTableFieldAction[] actionList = eventEvent.getActionList();
                    List<TableFieldsFeature> data = new ArrayList<>();
                    for (BitableTableFieldAction fieldAction : actionList) {
                        String fieldId = fieldAction.getFieldId();
                        String action = fieldAction.getAction();
                        if ("field_deleted".equals(action)) {
                            tableFieldsFeatureService.removeById(fieldId);
                        } else {
                            BitableTableFieldActionValue value = fieldAction.getAfterValue();
                            TableFieldsFeature feature = new TableFieldsFeature();
                            feature.setFieldId(fieldId);
                            feature.setFieldName(value.getName());
                            feature.setType(value.getType());
                            feature.setTableId(tableId);
                            data.add(feature);
                        }
                    }
                    tableFieldsFeatureService.saveOrUpdateBatch(data);
                }
            })
            // 多维表格记录变更，回调事件
            .onCustomizedEvent("drive.file.bitable_record_changed_v1", new CustomEventHandler() {
                @Override
                public void handle(EventReq event) throws Exception {
                    JSONObject jsonObject = JSONObject.parse(new String(event.getBody()));
                    String json = jsonObject.getString("encrypt");
                    String eventStr = EVENT_DISPATCHER.decryptEvent(json);
                    JSONObject object = JSONObject.parseObject(eventStr);
                    JSONObject header = object.getJSONObject("header");
                    log.info(header);
                    for (JSONObject j : object.getJSONObject("event").getList("action_list", JSONObject.class)) {
                        String action = j.getString("action");
                        JSONArray afterValue = j.getJSONArray("after_value");
                        afterValue.forEach(e -> {
                            JSONObject value = JSONObject.parseObject((String) e);
                            String field_id = value.getString("field_id");
                            String field_value = value.getString("field_value");
                        });
                        String recordId = j.getString("record_id");
                        log.info(afterValue);
                    }
                }
            })
            // 审批事件
            .onP2ApprovalUpdatedV4(new ApprovalService.P2ApprovalUpdatedV4Handler() {
                @Override
                public void handle(P2ApprovalUpdatedV4 event) throws Exception {
                    P2ApprovalUpdatedV4Data eventEvent = event.getEvent();
                    ApprovalEvent object = eventEvent.getObject();
                }
            })
            // 自定义事件
            .onCustomizedEvent("approval.approval.created_v4", new CustomEventHandler() {
                @Override
                public void handle(EventReq event) throws Exception {
                    JSONObject jsonObject = JSONObject.parse(new String(event.getBody()));
                    String json = jsonObject.getString("encrypt");
                    String eventStr = EVENT_DISPATCHER.decryptEvent(json);
                    JSONObject object = JSONObject.parseObject(eventStr);
                }
            })
            // 会话成员变更事件
            .onP2ChatMemberUserAddedV1(new ImService.P2ChatMemberUserAddedV1Handler() {
                @Override
                public void handle(P2ChatMemberUserAddedV1 event) throws Exception {
                    P2ChatMemberUserAddedV1Data eventData = event.getEvent();
                    String chatId = eventData.getChatId();
                    if (Objects.equals(CHAT_ID, chatId)) {
                        ChatMemberUser[] users = eventData.getUsers();
                        for (ChatMemberUser user : users) {
                            UserMemgerInfo info = new UserMemgerInfo();
                            info.setName(user.getName());
                            info.setMemberId(user.getUserId().getOpenId());
                            info.setTenantKey(user.getTenantKey());
                            userMemberService.saveOrUpdate(info);
                        }
                    }
                }
            })
            // 会话成员删除事件
            .onP2ChatMemberUserDeletedV1(new ImService.P2ChatMemberUserDeletedV1Handler() {
                @Override
                public void handle(P2ChatMemberUserDeletedV1 event) throws Exception {
                    P2ChatMemberUserDeletedV1Data eventData = event.getEvent();
                    String chatId = eventData.getChatId();
                    if (Objects.equals(CHAT_ID, chatId)) {
                        for (ChatMemberUser user : eventData.getUsers()) {
                            userMemberService.remove(Wrappers.<UserMemgerInfo>lambdaUpdate().eq(UserMemgerInfo::getMemberId, user.getUserId().getOpenId()));
                        }
                    }
                }
            })
            .build();
}
