package com.q.reminder.reminder.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.RdIssueBug;
import com.q.reminder.reminder.mapper.RdIssueBugMapping;
import com.q.reminder.reminder.service.RdIssueBugService;
import org.springframework.stereotype.Service;


/**
 * redmine问题记录表(RdIssueBug)表服务实现类
 *
 * @author makejava
 * @since 2023-05-11 18:18:54
 */
@Service
public class RdIssueBugServiceImpl extends MppServiceImpl<RdIssueBugMapping, RdIssueBug> implements RdIssueBugService {
    
}
