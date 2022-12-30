package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.WGroup;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;

import java.util.List;


/**
 * (WGroup)表服务接口
 *
 * @author makejava
 * @since 2022-12-28 10:35:13
 */
public interface WGroupService extends IService<WGroup>{

    List<RoleInvolvementVo> groupWorkload(WorkloadParamsVo params);
}
