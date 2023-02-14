package com.q.reminder.reminder.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.q.reminder.reminder.entity.WRoleGroupUser;
import com.q.reminder.reminder.vo.OptionVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;

import java.util.List;


/**
 * (WRoleGroupUser)表服务接口
 *
 * @author makejava
 * @since 2022-12-28 10:30:53
 */
public interface WRoleGroupUserService extends IMppService<WRoleGroupUser> {

    List<OptionVo> option(WorkloadParamsVo paramsVo);

}
