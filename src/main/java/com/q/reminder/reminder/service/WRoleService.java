package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.WRole;
import com.q.reminder.reminder.vo.OptionVo;
import com.q.reminder.reminder.vo.RoleGroupIdsVo;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;


/**
 * 角色(WRole)表服务接口
 *
 * @author makejava
 * @since 2022-12-29 09:16:14
 */
public interface WRoleService extends IService<WRole> {

    List<RoleInvolvementVo> roleInvolvement(WorkloadParamsVo params);

    List<RoleGroupIdsVo> listByGroupIds(List<OptionVo> result);
}
