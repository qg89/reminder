package com.q.reminder.reminder.util.feishu.cloud.table;

import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.enums.BatchCreateAppTableRecordUserIdTypeEnum;
import com.lark.oapi.service.bitable.v1.enums.BatchUpdateAppTableRecordUserIdTypeEnum;
import com.lark.oapi.service.bitable.v1.enums.ListAppTableRecordUserIdTypeEnum;
import com.lark.oapi.service.bitable.v1.model.*;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.cld.table.Table
 * @Description :
 * @date :  2023.02.23 14:42
 */
public class Table extends BaseFeishu {

    private static Table instance;

    private Table() {
        super();
    }

    public static synchronized Table getInstance() {
        if (instance == null) {
            instance = new Table();
        }
        return instance;
    }

    /**
     * 多为表格-记录-列出视图记录
     *
     * @param client
     * @return
     */
    public List<AppTableRecord> listTableRecords(TTableInfo vo) {
        List<AppTableRecord> resList = new ArrayList<>();
        ListAppTableRecordReq req = ListAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .viewId(vo.getViewId())
                .filter(vo.getFilter())
                .userIdType(ListAppTableRecordUserIdTypeEnum.OPEN_ID)
                .build();
        ListAppTableRecordResp resp;
        ListAppTableRecordRespBody respData = new ListAppTableRecordRespBody();
        do {
            String pageToken = respData.getPageToken();
            if (StringUtils.isNotBlank(pageToken)) {
                req.setPageToken(pageToken);
            }
            try {
                resp = CLIENT.bitable().appTableRecord().list(req, REQUEST_OPTIONS);
            } catch (Exception e) {
                throw new FeishuException(e, this.getClass().getName() + " 多为表格-记录-列出视图记录异常");
            }
            if (!resp.success()) {
                return resList;
            }
            respData = resp.getData();
            AppTableRecord[] items = respData.getItems();
            if (items == null) {
                return resList;
            }
            resList.addAll(Arrays.stream(items).toList());
        } while (resp.getCode() == 0 && respData.getHasMore());
        return resList;
    }

    /**
     * 多为表格-记录-批量创建记录
     *
     * @param client
     * @param vo
     * @throws Exception
     */
    public void batchCreateTableRecords(Client client, TTableInfo vo, AppTableRecord[] records) {
        BatchCreateAppTableRecordReq req = BatchCreateAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .userIdType(BatchCreateAppTableRecordUserIdTypeEnum.OPEN_ID)
                .batchCreateAppTableRecordReqBody(BatchCreateAppTableRecordReqBody.newBuilder()
                        .records(records)
                        .build())
                .build();
        try {
            client.bitable().appTableRecord().batchCreate(req, REQUEST_OPTIONS);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 多为表格-记录-批量创建记录异常");
        }
    }

    /**
     * 多为表格-记录-批量删除记录
     *
     * @param records
     */
    public void batchDeleteTableRecords(TTableInfo vo, String[] records) {
        // 创建请求对象
        BatchDeleteAppTableRecordReq req = BatchDeleteAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .batchDeleteAppTableRecordReqBody(BatchDeleteAppTableRecordReqBody.newBuilder().records(records).build())
                .build();
        try {
            CLIENT.bitable().appTableRecord().batchDelete(req, REQUEST_OPTIONS);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 多为表格-记录-批量删除记录异常");
        }
    }

    /**
     * 多为表格-记录-批量更新记录
     *
     * @param vo
     * @throws Exception
     */
    public void batchUpdateTableRecords(TTableInfo vo, AppTableRecord[] records) {
        BatchUpdateAppTableRecordReqBody reqBody = BatchUpdateAppTableRecordReqBody.newBuilder().records(records).build();
        BatchUpdateAppTableRecordReq req = BatchUpdateAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .userIdType(BatchUpdateAppTableRecordUserIdTypeEnum.OPEN_ID)
                .batchUpdateAppTableRecordReqBody(reqBody)
                .build();
        try {
            CLIENT.bitable().appTableRecord().batchUpdate(req, REQUEST_OPTIONS);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 多为表格-记录-批量更新记录异常");
        }
    }

    /**
     * 多为表格-记录-批量创建记录
     *
     * @param vo
     * @throws Exception
     */
    public void batchCreateTableRecords(TTableInfo vo, AppTableRecord[] records) {
        BatchCreateAppTableRecordReq req = BatchCreateAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .userIdType(BatchCreateAppTableRecordUserIdTypeEnum.OPEN_ID)
                .batchCreateAppTableRecordReqBody(BatchCreateAppTableRecordReqBody.newBuilder()
                        .records(records)
                        .build())
                .build();
        try {
            CLIENT.bitable().appTableRecord().batchCreate(req, REQUEST_OPTIONS);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 多为表格-记录-批量创建记录异常");
        }
    }
}
