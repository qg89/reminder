package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
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
public interface RedmineUserInfoMapping extends BaseMapper<RedmineUserInfo> {
    List<RoleInvolvementVo> roleInvolvement(@Param("pId") String pId, @Param("year")  String year);

    List<RoleInvolvementVo> residualWorkload(@Param("pId") String pId, @Param("year")  String year);
}
