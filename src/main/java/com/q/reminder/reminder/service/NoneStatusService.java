package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.NoneStatus;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.UserService
 * @Description :
 * @date :  2022.09.23 14:30
 */
public interface NoneStatusService extends IService<NoneStatus> {
    List<String> queryUnInStatus();
}
