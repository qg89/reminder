package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.GroupProject;
import com.q.reminder.reminder.mapper.GroupProjectMapping;
import com.q.reminder.reminder.service.GroupProjectService;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.GroupProjectServiceImpl
 * @Description :
 * @date :  2022.12.21 19:23
 */
@Service
public class GroupProjectServiceImpl extends MppServiceImpl<GroupProjectMapping, GroupProject> implements GroupProjectService {
}
