package com.q.reminder.reminder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.mapper.ProjectInfoMapping;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.vo.ProjectInfoVo;
import com.q.reminder.reminder.vo.WeeklyByProjectVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.MemberInfoServiceImpl
 * @Description :
 * @date :  2022.09.27 13:24
 */
@Service
public class ProjectInfoServiceImpl extends ServiceImpl<ProjectInfoMapping, RProjectInfo> implements ProjectInfoService {
    @Override
    public List<WeeklyProjectVo> getWeeklyDocxList(int weekNumber, String id) {
        return baseMapper.getWeeklyDocxList(weekNumber, id);
    }

    @Override
    public List<WeeklyByProjectVo> weeklyByProjectList(String id, String name) {
        return baseMapper.weeklyByProjectList(id, name);
    }

    @Override
    public List<ProjectInfoVo> listToArray(List<RProjectInfo> list) {
        List<ProjectInfoVo> res = new ArrayList<>();
        list.forEach(info -> {
            BeanUtil.beanToMap(info).forEach((k, v) -> {
                if ("id".equals(k)) {
                    return;
                }
                ProjectInfoVo vo = new ProjectInfoVo();
                vo.setValue(v);
                vo.setKey(k);
                vo.setLabel(map().get(k));
                vo.setColumnType("input");
                Object radio = radio().get(k);
                if (!Objects.equals(radio, null)) {
                    vo.setColumnType("radio");
                    vo.setColumnDesc(radio);
                }
                if ("productMemberId".equals(k) || "pmOu".equals(k)) {
                    vo.setColumnType("api");
                    vo.setColumnDesc(Map.of("url", "/p/member", "method", "GET", "return", "array"));
                }
                if ("sendGroupChatId".equals(k)) {
                    vo.setColumnType("api");
                    vo.setColumnDesc(Map.of("url", "/p/group", "method", "GET", "return", "array"));
                }
                res.add(vo);
            });
        });
        return res;
    }

    private Map<String, Object> radio() {
        List<Map<String, String>> radios = List.of(Map.of("0", "是"), Map.of("1", "否"));
        return Map.of(
                "syncFeature", radios,
                "isSendGroup", radios,
                "weeklyCopyType", radios,
                "weeklyType", radios,
                "wikiType", radios,
                "redmineType", radios,
                "isDelete", radios
        );
    }

    private Map<String, String> map() {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("pId", "redmine项目ID");
        resMap.put("pKey", "redmine项目key");
        resMap.put("pName", "redmine项目名称");
        resMap.put("featureToken", "需求管理表token");
        resMap.put("syncFeature", "同步需求管理表");
        resMap.put("projectShort_n", "项目短名称");
        resMap.put("pmOu", "项目经理Open_Id");
        resMap.put("pmKey", "项目经理redminekey");
        resMap.put("redmineUrl", "redmineURL");
        resMap.put("startDay", "项目开始时间");
        resMap.put("sendGroupChat", "发送群对应的ID");
        resMap.put("isSendGroup", "是否发送需求群");
        resMap.put("productMember_", "产品经理飞书ID");
        resMap.put("folderToken", "项目周报文件夹token");
        resMap.put("weeklyCopyTyp", "项目周报是否复制");
        resMap.put("weeklyType", "项目周报类型");
        resMap.put("wikiType", "生成状态");
        resMap.put("wikiToken", "知识库token");
        resMap.put("redmineType", "redmine类型");
        resMap.put("isDelete", "删除标识");
        return resMap;
    }
}
