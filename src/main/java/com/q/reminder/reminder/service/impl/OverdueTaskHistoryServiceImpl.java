package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.OverdueTaskHistory;
import com.q.reminder.reminder.mapper.OverdueTaskHistoryMapping;
import com.q.reminder.reminder.service.OverdueTaskHistoryService;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.MemberInfoServiceImpl
 * @Description :
 * @date :  2022.09.27 13:24
 */
@Service
public class OverdueTaskHistoryServiceImpl extends ServiceImpl<OverdueTaskHistoryMapping, OverdueTaskHistory> implements OverdueTaskHistoryService {
}
