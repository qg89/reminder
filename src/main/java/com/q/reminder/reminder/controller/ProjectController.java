package com.q.reminder.reminder.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.bitable.v1.model.AppTableField;
import com.lark.oapi.service.bitable.v1.model.AppTableFieldProperty;
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldReq;
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldResp;
import com.q.reminder.reminder.entity.*;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.service.impl.FeishuService;
import com.q.reminder.reminder.vo.OptionVo;
import com.q.reminder.reminder.vo.ProjectInfoVo;
import com.q.reminder.reminder.vo.RProjectReaVo;
import com.q.reminder.reminder.vo.base.ReturnT;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.ProjectController
 * @Description :
 * @date :  2022.12.21 19:13
 */
@RestController
@RequestMapping("/p")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectInfoService projectInfoService;
    private final GroupProjectService groupProjectService;
    private final UserPService userPService;
    private final GroupInfoService groupInfoService;
    private final LoginService loginService;
    private final UserMemberService userMemberService;
    private final FeishuService feishuService;
    private final TableFieldsFeatureService fieldsFeatureService;

    @GetMapping("/i")
    public ReturnT<List<List<ProjectInfoVo>>> list() {
        LambdaQueryWrapper<RProjectInfo> lq = Wrappers.lambdaQuery();
        lq.orderByDesc(RProjectInfo::getUpdateTime);
        List<RProjectInfo> list = projectInfoService.list(lq);
        Map<String, String> userMap = userMemberService.list().stream().collect(Collectors.toMap(UserMemgerInfo::getMemberId, UserMemgerInfo::getName));
        Map<String, String> groupMap = groupInfoService.list().stream().collect(Collectors.toMap(FsGroupInfo::getChatId, FsGroupInfo::getName));
        List<List<ProjectInfoVo>> res = projectInfoService.listToArray(list, userMap, groupMap);
        return new ReturnT<>(res);
    }

    @GetMapping("/d/{id}")
    public ReturnT<Boolean> del(@PathVariable("id") String id) {
        return new ReturnT<>(projectInfoService.removeById(id));
    }

    @PostMapping("/s")
    public ReturnT<String> save(@RequestBody RProjectReaVo info) {
        projectInfoService.save(info);
        if (!saveRea(info)) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    @GetMapping("/t")
    public Object t() throws Exception {
        // 创建请求对象
        ListAppTableFieldReq req = ListAppTableFieldReq.newBuilder()
                .appToken("bascnrkdLGoUftLgM7fvME7ly5c")
                .tableId("tbld61CFebNfZ6M6")
                .viewId("vewk6iANmq")
                .build();

        // 发起请求
        // 如开启了Sdk的token管理功能，就无需调用 RequestOptions.newBuilder().tenantAccessToken("t-xxx").build()来设置租户token了
        ListAppTableFieldResp resp = feishuService.client().bitable().appTableField().list(req, RequestOptions.newBuilder()
                .build());
        List<TableFieldsFeature> data = new ArrayList<>() {
        };
        for (AppTableField item : resp.getData().getItems()) {
            AppTableFieldProperty property = item.getProperty();
            JSONObject from = JSONObject.from(property);
            TableFieldsFeature feature = new TableFieldsFeature();
            feature.setFieldId(item.getFieldId());
            feature.setFieldName(item.getFieldName());
            feature.setType(item.getType());
            feature.setIsPrimary(String.valueOf(item.getIsPrimary()));
            feature.setUiType(item.getUiType());
            feature.setTableId("tbld61CFebNfZ6M6");
//            feature.setProperty(from.toJSONString(JSONWriter.Feature.IgnoreNoneSerializable));
            data.add(feature);
        }
       return fieldsFeatureService.saveOrUpdateBatch(data);
    }

    @PostMapping("/e")
    public ReturnT<String> edit(@RequestBody RProjectReaVo info) {
        if ("1".equals(info.getIsDelete())) {
            projectInfoService.removeById(info);
            return ReturnT.SUCCESS;
        }
        projectInfoService.updateInfo(info);
        if (!saveRea(info)) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @PostMapping("/r")
    public ReturnT<String> rea(@RequestBody RProjectReaVo vo) {
        if (!saveRea(vo)) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @GetMapping("/info")
    public ReturnT<List<ProjectInfoVo>> info() {
        return new ReturnT<>(projectInfoService.listInfo());
    }

    private Boolean saveRea(RProjectReaVo vo) {
        String pId = vo.getPid();
        String chatId = vo.getChatId();
        String userId = vo.getUserId();
        if (StringUtils.isBlank(pId) || projectInfoService.getOne(Wrappers.<RProjectInfo>lambdaQuery().eq(RProjectInfo::getPid, pId)) == null) {
            return Boolean.FALSE;
        }
        if (StringUtils.isNotBlank(chatId)) {
            groupProjectService.saveOrUpdateByMultiId(new GroupProject(chatId, pId));
        }
        if (StringUtils.isNotBlank(userId)) {
            userPService.saveOrUpdateByMultiId(new UserP(userId, pId));
        }
        return Boolean.TRUE;
    }

    @GetMapping("/o")
    public ReturnT<Map<String, Object>> option() {
        Map<String, Object> map = new HashMap<>(4);
        LambdaQueryWrapper<FsGroupInfo> gp = Wrappers.lambdaQuery();
        gp.select(FsGroupInfo::getChatId, FsGroupInfo::getName);
        List<FsGroupInfo> fsGroupInfoList = groupInfoService.list(gp);
        LambdaQueryWrapper<User> u = Wrappers.lambdaQuery();
        u.select(User::getId, User::getName);
        List<User> userList = loginService.list(u);
        map.put("group", fsGroupInfoList);
        map.put("user", userList);
        return new ReturnT<>(map);
    }

    @GetMapping("/group")
    public ReturnT<List<OptionVo>> group() {
        LambdaQueryWrapper<FsGroupInfo> lq = Wrappers.lambdaQuery();
        lq.select(FsGroupInfo::getChatId, FsGroupInfo::getName);
        List<FsGroupInfo> list = groupInfoService.list(lq);
        List<OptionVo> res = new ArrayList<>();
        list.forEach(i -> {
            OptionVo vo = new OptionVo();
            vo.setId(i.getChatId());
            vo.setName(i.getName());
            res.add(vo);
        });
        return new ReturnT<>(res);
    }

    @GetMapping("/member")
    public ReturnT<List<OptionVo>> member() {
        LambdaQueryWrapper<UserMemgerInfo> lq = Wrappers.lambdaQuery();
        lq.select(UserMemgerInfo::getMemberId, UserMemgerInfo::getName);
        lq.eq(UserMemgerInfo::getResign, "0");
        List<OptionVo> res = new ArrayList<>();
        userMemberService.list(lq).forEach(i -> {
            OptionVo vo = new OptionVo();
            vo.setId(i.getMemberId());
            vo.setName(i.getName());
            res.add(vo);
        });
        return new ReturnT<>(res);
    }
}
