package com.q.reminder.reminder.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.mapper.RedmineUserInfoMapping
 * @Description :
 * @date :  2022.10.27 16:19
 */
@Mapper
public interface RedmineUserInfoMapping extends MppBaseMapper<RedmineUserInfo> {
    List<RoleInvolvementVo> roleInvolvement(@Param("vo") WorkloadParamsVo params);

    List<RoleInvolvementVo> residualWorkload(@Param("vo") WorkloadParamsVo params);

    List<RoleInvolvementVo> groupUserWorkload(@Param("vo") WorkloadParamsVo params);
}
