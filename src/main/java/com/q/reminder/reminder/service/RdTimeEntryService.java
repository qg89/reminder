package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.vo.OptionVo;
import com.q.reminder.reminder.vo.OvertimeVo;
import com.q.reminder.reminder.vo.UserInfoTimeVo;
import com.q.reminder.reminder.vo.UserInfoWrokVo;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import com.q.reminder.reminder.vo.params.UserInfoParamsVo;

import java.util.List;


/**
 * (RdTimeEntry)表服务接口
 *
 * @author makejava
 * @since 2023-01-19 11:47:40
 */
public interface RdTimeEntryService extends IMppService<RdTimeEntry>{

    List<OvertimeVo> listOvertime(ProjectParamsVo ym);

    IPage<UserInfoWrokVo> userinfoList(Page<UserInfoWrokVo> page, UserInfoParamsVo vo);

    IPage<UserInfoTimeVo> userTimeList(Page<UserInfoTimeVo> page, UserInfoParamsVo vo);

    List<OptionVo> userOption();
}
