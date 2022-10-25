package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.vo.SendUserByGroupVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.mapper.UserMapping
 * @Description :
 * @date :  2022.09.23 14:29
 */
@Mapper
public interface UserMapping extends BaseMapper<UserMemgerInfo> {

    List<SendUserByGroupVo> queryUserGroupList();

}
