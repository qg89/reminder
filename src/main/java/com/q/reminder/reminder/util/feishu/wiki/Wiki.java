package com.q.reminder.reminder.util.feishu.wiki;

import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.wiki.v2.model.*;
import com.q.reminder.reminder.util.feishu.BaseFeishu;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.wiki.Wiki
 * @Description :
 * @date :  2023.02.23 11:58
 */
public class Wiki extends BaseFeishu {

    private static Wiki instance;

    private Wiki() {
        super();
    }

    public static synchronized Wiki getInstance() {
        if (instance == null) {
            instance = new Wiki();
        }
        return instance;
    }

    /**
     * 复制知识空间节点
     *
     * @param projectToken
     * @param title
     * @return
     * @throws Exception
     */
    public Node syncSpacesWiki(String projectToken, String title) throws Exception {
        CopySpaceNodeReq req = CopySpaceNodeReq.newBuilder()
                .copySpaceNodeReqBody(CopySpaceNodeReqBody.newBuilder()
                        .targetParentToken(projectToken)
                        .targetSpaceId("7046680616087126018")
                        .title(title)
                        .build())
                .nodeToken("wikcnXpXCgmL3E7vdbM1TiwXiGc")
                .spaceId("7046680616087126018")
                .build();
        CopySpaceNodeResp resp = CLIENT.wiki().spaceNode().copy(req);
        return resp.getData().getNode();
    }

    /**
     * 获取知识空间节点信息
     *
     * @param token
     * @return
     * @throws Exception
     */
    public Node getSpacesNode(String token) throws Exception {
        GetNodeSpaceReq req = GetNodeSpaceReq.newBuilder()
                .token(token)
                .build();
        GetNodeSpaceResp resp = CLIENT.wiki().space().getNode(req);
        return resp.getData().getNode();
    }

    /**
     * 知识空间获取文件详情
     *
     * @param wikiToken
     * @return
     * @throws Exception
     */
    public Node getNodeSpace(String wikiToken) throws Exception {
        GetNodeSpaceReq req = GetNodeSpaceReq.newBuilder()
                .token(wikiToken)
                .build();
        GetNodeSpaceResp resp = CLIENT.wiki().space().getNode(req, RequestOptions.newBuilder()
                .build());
        if (resp.success()) {
            return resp.getData().getNode();
        }
        return null;
    }
}
