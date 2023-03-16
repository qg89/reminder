package com.q.reminder.reminder.util.feishu.approval;

import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.approval.v4.model.*;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.ApprovalSampleVo;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.approval.NativeApproval
 * @Description :
 * @date :  2023.02.23 15:16
 */
public class NativeApproval extends BaseFeishu {

    private static NativeApproval instance;

    private NativeApproval() {
        super();
    }

    public static synchronized NativeApproval getInstance() {
        if (instance == null) {
            instance = new NativeApproval();
        }
        return instance;
    }

    /**
     * 审批-获取单个审批实例详情
     *
     * @param instanceId
     * @return
     * @throws Exception
     */
    public InstanceComment[] getInstanceSample(String instanceId) throws Exception {
        // 创建请求对象
        GetInstanceReq req = GetInstanceReq.newBuilder()
                .instanceId(instanceId)
                .build();

        // 发起请求
        // 如开启了Sdk的token管理功能，就无需调用 RequestOptions.newBuilder().tenantAccessToken("t-xxx").build()来设置租户token了
        GetInstanceResp resp = CLIENT.approval().instance().get(req, RequestOptions.newBuilder()
                .build());
        if (resp.success()) {
            return resp.getData().getCommentList();
        }
        return null;
    }

    /**
     * 审批-批量获取审批实例 ID
     */
    public String[] listInstanceSample(ApprovalSampleVo vo) throws Exception {
        ListInstanceReq req = ListInstanceReq.newBuilder()
                .approvalCode(vo.getApprovalCode())
                .startTime(vo.getStartTime())
                .endTime(vo.getEndTime())
                .build();

        // 发起请求
        ListInstanceResp resp = CLIENT.approval().instance().list(req, RequestOptions.newBuilder()
                .build());
        if (resp.success()) {
            return resp.getData().getInstanceCodeList();
        }
        return null;
    }
}
