package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.FsGroupInfo;
import com.q.reminder.reminder.vo.ChatProjectVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.mapper.MemberInfoMapper
 * @Description :
 * @date :  2022.09.27 13:24
 */
@Mapper
public interface GroupInfoMapping extends BaseMapper<FsGroupInfo> {
    List<ChatProjectVo> listByProject(@Param("pKey") String pKey);
}
