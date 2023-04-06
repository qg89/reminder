package com.q.reminder.reminder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.mapper.ProjectInfoMapping;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.vo.ProjectInfoVo;
import com.q.reminder.reminder.vo.RProjectReaVo;
import com.q.reminder.reminder.vo.WeeklyByProjectVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    public List<List<ProjectInfoVo>> listToArray(List<RProjectInfo> list) {
        List<List<ProjectInfoVo>> resDate = new ArrayList<>();
        List<String> removeColumn = List.of("id", "updateTime", "createTime");
        list.forEach(info -> {
            List<ProjectInfoVo> res = new ArrayList<>();
            BeanUtil.beanToMap(info).forEach((k, v) -> {
                if (removeColumn.contains(k)) {
                    return;
                }
                ProjectInfoVo vo = new ProjectInfoVo();
                vo.setValue(v);
                extracted(k, vo);
                res.add(vo);
            });
            resDate.add(res);
        });
        return resDate;
    }

    private void extracted(String k, ProjectInfoVo vo) {
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
        if ("startDay".equals(k)) {
            vo.setColumnType("date");
        }
    }

    @Override
    public List<ProjectInfoVo> listInfo() {
        List<ProjectInfoVo> res = new ArrayList<>();
        map().forEach((k, v) -> {
            ProjectInfoVo vo = new ProjectInfoVo();
            extracted(k, vo);
            res.add(vo);
        });
        return res;
    }

    @Override
    @Cacheable(cacheNames = RedisKeyContents.REDMINE_PROJECT_ALL, key = "'projectAll'")
    public List<RProjectInfo> listAll() {
        return list();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyContents.REDMINE_PROJECT_ALL, key = "#info.getPkey()")
    public void updateInfo(RProjectReaVo info) {
        update(info, Wrappers.<RProjectInfo>lambdaUpdate().eq(RProjectInfo::getPkey, info.getPkey()));
    }

    @Override
    @Cacheable(cacheNames = RedisKeyContents.REDMINE_PROJECT_KEY, key = "#prjctKey", unless = "#result == null")
    public RProjectInfo projectInfoByPrjctKey(String prjctKey) {
        LambdaQueryWrapper<RProjectInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(RProjectInfo::getPkey, prjctKey);
        return getOne(lambdaQueryWrapper);
    }


    private Map<String, Object> radio() {
        List<Map<String, String>> radios = List.of(Map.of("0", "是"), Map.of("1", "否"));
        return Map.of(
//                "syncFeature", radios,
                "isSendGroup", radios,
                "weeklyCopyType", radios,
                "weeklyType", radios,
                "wikiType", radios,
                "redmineType", List.of(Map.of("1", "旧"), Map.of("2", "新")),
                "isDelete", List.of(Map.of("1", "是"), Map.of("0", "否"))
        );
    }

    private Map<String, String> map() {
        Map<String, String> resMap = new LinkedHashMap<>();
        resMap.put("pid", "redmine项目ID");
        resMap.put("pkey", "redmine项目Key");
        resMap.put("pname", "redmine项目名称");
//        resMap.put("featureToken", "需求管理表Token");
//        resMap.put("syncFeature", "同步需求管理表");
        resMap.put("projectShortName", "项目短名称");
        resMap.put("pmOu", "项目经理");
        resMap.put("pmKey", "项目经理RedmineKey");
        resMap.put("redmineUrl", "RedmineURL");
        resMap.put("startDay", "项目开始时间");
        resMap.put("sendGroupChatId", "项目需求群");
        resMap.put("isSendGroup", "发送需求群");
        resMap.put("productMemberId", "产品经理");
        resMap.put("folderToken", "项目周报文件夹Token");
        resMap.put("weeklyCopyType", "复制项目周报");
        resMap.put("weeklyType", "自动生成项目周报");
        resMap.put("wikiType", "自动复制标准过程表");
        resMap.put("wikiToken", "知识库Token");
        resMap.put("redmineType", "Redmine类型");
        resMap.put("isDelete", "删除标识");
        return resMap;
    }
}
