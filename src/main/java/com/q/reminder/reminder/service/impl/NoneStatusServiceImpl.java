package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.NoneStatus;
import com.q.reminder.reminder.mapper.NoneStatusMapping;
import com.q.reminder.reminder.service.NoneStatusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.NoneStatusServiceImpl
 * @Description :
 * @date :  2022.09.27 15:30
 */
@Service
public class NoneStatusServiceImpl extends ServiceImpl<NoneStatusMapping, NoneStatus> implements NoneStatusService {
    @Override
    public List<String> queryUnInStatus(Integer expiredDays) {
        LambdaQueryWrapper<NoneStatus> lqw = new LambdaQueryWrapper<>();
        lqw.in(NoneStatus::getExpiredDays, expiredDays);
        lqw.select(NoneStatus::getNoneStatus);
        return this.listObjs(lqw, (Object::toString));
    }
}
