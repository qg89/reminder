package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.mapper.UserMapping;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.vo.SendUserByGroupVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
        List<UserMemgerInfo> data = new ArrayList<>(membersByFeiShu);
        // 现有在职人员
        List<UserMemgerInfo> userMemgerInfoList = this.list(Wrappers.<UserMemgerInfo>lambdaQuery().eq(UserMemgerInfo::getResign, "0"));
        Map<String, List<UserMemgerInfo>> memberIdMap = userMemgerInfoList.stream().collect(Collectors.groupingBy(UserMemgerInfo::getMemberId));
        Map<String, List<UserMemgerInfo>> feiShuMemberMap = data.stream().collect(Collectors.groupingBy(UserMemgerInfo::getMemberId));

        // 新增
        data.removeIf(e -> memberIdMap.containsKey(e.getMemberId()));
        this.saveBatch(data);

        // 离职
        userMemgerInfoList.removeIf(e -> feiShuMemberMap.containsKey(e.getMemberId()));
        if (!CollectionUtils.isEmpty(userMemgerInfoList)) {
            userMemgerInfoList.forEach(e -> e.setResign("1"));
            this.updateBatchById(userMemgerInfoList);
        }
        return Boolean.TRUE;
    }

    @Override
    public  List<SendUserByGroupVo> queryUserGroupList() {
        return baseMapper.queryUserGroupList();
    }
}
