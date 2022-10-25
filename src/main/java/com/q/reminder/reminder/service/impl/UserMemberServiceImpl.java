package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.mapper.UserMapping;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.vo.SendUserByGroupVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.UserServiceImpl
 * @Description :
 * @date :  2022.09.23 14:32
 */
@Log4j2
@Service
public class UserMemberServiceImpl extends ServiceImpl<UserMapping, UserMemgerInfo> implements UserMemberService {
    @Override
    public Boolean saveOrUpdateBatchAll(List<UserMemgerInfo> membersByFeiShu) {
        // 现有在职人员
        List<UserMemgerInfo> memgerInfos = this.list(Wrappers.<UserMemgerInfo>lambdaQuery().eq(UserMemgerInfo::getResign, "0"));

        // 新增
        Map<String, List<UserMemgerInfo>> memberIdMap = memgerInfos.stream().collect(Collectors.groupingBy(UserMemgerInfo::getMemberId));
        membersByFeiShu.removeIf(e -> memberIdMap.containsKey(e.getMemberId()));
        this.saveBatch(membersByFeiShu);

        // 离职
        Map<String, List<UserMemgerInfo>> feiShuMemberMap = membersByFeiShu.stream().collect(Collectors.groupingBy(UserMemgerInfo::getMemberId));
        memgerInfos.removeIf(e -> feiShuMemberMap.containsKey(e.getMemberId()));
        if (!memgerInfos.isEmpty()) {
            memgerInfos.forEach(e -> e.setResign("1"));
            this.updateBatchById(memgerInfos);
        }
//
//        this.update(Wrappers.<UserMemgerInfo>lambdaUpdate().set(UserMemgerInfo::getResign, "1"));
//        Map<String, String> userMap = membersByChats.stream().collect(Collectors.toMap(UserMemgerInfo::getMemberId, UserMemgerInfo::getUserName));
//        List<UserMemgerInfo> userMemgerInfos = this.list();
//        Set<String> userSet = userMemgerInfos.stream().map(UserMemgerInfo::getMemberId).collect(Collectors.toSet());
//        // 当人员新增、初始化时
//        if (membersByChats.size() > userMemgerInfos.size()) {
//            List<UserMemgerInfo> inserNewMember = membersByChats.stream().peek(e -> e.setResign("0")).filter(e -> userSet.contains(e.getMemberId())).collect(Collectors.toList());
//            boolean saveBatch = this.saveBatch(inserNewMember);
//            if (!saveBatch) {
//                log.info("新增员工,保存失败");
//            }
//            log.info("新增员工,保存成功");
//        }
//        // 重新查询插入后的人员列表，并且保存redmine用户名为空的
//        List<UserMemgerInfo> userMemgerInfosNew = baseMapper.selectList(Wrappers.<UserMemgerInfo>lambdaQuery().eq(UserMemgerInfo::getResign, "0"));
//        if (userMemgerInfosNew.size() == membersByChats.size()) {
//            userMemgerInfosNew.stream().filter(e -> StringUtils.isBlank(e.getUserName())).forEach(e -> {
//                e.setUserName(userMap.get(e.getMemberId()));
//                baseMapper.updateById(e);
//            });
//        }
        return Boolean.TRUE;
    }

    @Override
    public  List<SendUserByGroupVo> queryUserGroupList() {
        return baseMapper.queryUserGroupList();
    }
}
