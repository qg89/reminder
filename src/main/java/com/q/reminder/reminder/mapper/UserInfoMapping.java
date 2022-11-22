package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.User;
import com.q.reminder.reminder.vo.UserInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.mapper.UserMapper
 * @Description :
 * @date :  2022.11.17 14:08
 */
@Mapper
public interface UserInfoMapping extends BaseMapper<User> {
    UserInfoVo userInfo(@Param("username") String username);
}
