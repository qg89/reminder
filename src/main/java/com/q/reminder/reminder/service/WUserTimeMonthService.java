package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.WUserTimeMonth;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.UserTimeMonthRatioVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;

import java.util.List;


/**
 * (WUserTimeMonth)表服务接口
 *
 * @author makejava
 * @since 2022-12-28 10:32:51
 */
public interface WUserTimeMonthService extends IService<WUserTimeMonth>{

    List<RoleInvolvementVo> inputRatio(WorkloadParamsVo params);

    Boolean inputRatioEdit(List<UserTimeMonthRatioVo> enity);
}
