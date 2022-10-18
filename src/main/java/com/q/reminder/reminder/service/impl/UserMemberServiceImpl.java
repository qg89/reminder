package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.mapper.UserMapping;
import com.q.reminder.reminder.service.UserMemberService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public Boolean saveOrupdateBatchByNameIsNull(List<UserMemgerInfo> membersByChats) {
        Map<String, String> userMap = membersByChats.stream().peek(e -> e.setUserName(new StringBuilder(e.getName()).insert(1, " ").toString())).collect(Collectors.toMap(UserMemgerInfo::getMemberId, UserMemgerInfo::getUserName));
        List<UserMemgerInfo> userMemgerInfos = baseMapper.selectList(new QueryWrapper<>());
        Set<String> userSet = userMemgerInfos.stream().map(UserMemgerInfo::getMemberId).collect(Collectors.toSet());
        // 当人员新增、初始化时
        if (membersByChats.size() > userMemgerInfos.size()) {
            List<UserMemgerInfo> inserNewMember = membersByChats.stream().filter(e -> userSet.contains(e.getMemberId())).collect(Collectors.toList());
            boolean saveBatch = this.saveBatch(inserNewMember);
            if (!saveBatch) {
                log.info("新增员工,保存失败");
            }
            log.info("新增员工,保存成功");
        }
        // 重新查询插入后的人员列表，并且保存redmine用户名为空的
        List<UserMemgerInfo> userMemgerInfosNew = baseMapper.selectList(new QueryWrapper<>());
        if (userMemgerInfosNew.size() == membersByChats.size()) {
            userMemgerInfosNew.stream().filter(e -> StringUtils.isBlank(e.getUserName())).forEach(e -> {
                e.setUserName(userMap.get(e.getMemberId()));
                baseMapper.updateById(e);
            });
        }
        return Boolean.TRUE;
    }
}
