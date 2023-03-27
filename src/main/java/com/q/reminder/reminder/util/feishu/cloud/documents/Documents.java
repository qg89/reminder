package com.q.reminder.reminder.util.feishu.cloud.documents;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.docx.v1.enums.BatchUpdateDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.enums.ListDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.enums.PatchDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.model.*;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.WeeklyProjectVo;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.cld.documents.Block
 * @Description :
 * @date :  2023.02.23 11:43
 */
public class Documents extends BaseFeishu {

    private  static Documents instance;
    private Documents(){
        super();
    }
    public static synchronized Documents getInstance(){
        if (instance == null) {
            instance = new Documents();
        }
        return instance;
    }

    /**
     * 更新块
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public Boolean updateBlocks( WeeklyProjectVo vo) throws Exception {
        UpdateBlockRequest update = UpdateBlockRequest.newBuilder().build();
        update.setReplaceImage(ReplaceImageRequest.newBuilder()
                .token(vo.getImageToken())
                .build());
        PatchDocumentBlockResp patch = CLIENT.docx().documentBlock().patch(PatchDocumentBlockReq.newBuilder()
                .documentId(vo.getFileToken())
                .blockId(vo.getBlockId())
                .documentRevisionId(-1)
                .userIdType(PatchDocumentBlockUserIdTypeEnum.USER_ID)
                .updateBlockRequest(update)
                .build(), RequestOptions.newBuilder().tenantAccessToken(TENANT_ACCESS_TOKEN).build());
        if (patch.success()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 批量更新块
     * @param vo
     * @param updateBlockRequests
     * @return
     * @throws Exception
     */
    public Boolean batchUpdateBlocks(WeeklyProjectVo vo, UpdateBlockRequest[] updateBlockRequests) throws Exception {
        BatchUpdateDocumentBlockReq req = BatchUpdateDocumentBlockReq.newBuilder()
                .documentId(vo.getFileToken())
                .documentRevisionId(-1)
                .userIdType(BatchUpdateDocumentBlockUserIdTypeEnum.USER_ID)
                .batchUpdateDocumentBlockReqBody(BatchUpdateDocumentBlockReqBody.newBuilder().requests(updateBlockRequests).build())
                .build();

        BatchUpdateDocumentBlockResp resp = CLIENT.docx().documentBlock().batchUpdate(req, RequestOptions.newBuilder().tenantAccessToken(TENANT_ACCESS_TOKEN).build());
        if (resp.success()) {
            return Boolean.TRUE;
        } else {
            JSONObject json = new JSONObject();
            json.put("text", "项目名称： " + vo.getProjectShortName() + "，msg：" + resp.getMsg() + ", error：" + resp.getError());
//                sendAdmin(client, json, List.of("ou_35e03d4d8754dd35fed26c26849c85ab"));
        }
        return Boolean.FALSE;
    }

    /**
     * 获取文档所有块
     *
     * @param vo
     * @return
     */
    public JSONArray blocks(WeeklyProjectVo vo) throws Exception {
        // 构建client

        // 创建请求对象
        ListDocumentBlockReq req = ListDocumentBlockReq.newBuilder()
                .documentId(vo.getFileToken())
                .pageSize(500)
                .documentRevisionId(-1)
                .userIdType(ListDocumentBlockUserIdTypeEnum.USER_ID)
                .build();

        // 发起请求
        ListDocumentBlockResp resp = CLIENT.docx().documentBlock().list(req, RequestOptions.newBuilder().tenantAccessToken(TENANT_ACCESS_TOKEN).build());
        // 业务数据处理
        if (resp != null && resp.getCode() == 0) {
            JSONObject result = JSONObject.parseObject(Jsons.DEFAULT.toJson(resp.getData()));
            return result.getJSONArray("items");
        }
        return new JSONArray();
    }
}
