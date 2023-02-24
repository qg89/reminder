package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.vo.WeeklyByProjectVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
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
public interface ProjectInfoMapping extends BaseMapper<RProjectInfo> {

    List<WeeklyProjectVo> getWeeklyDocxList(@Param("weekNumber") int weekNumber, @Param("id") String id);

    List<WeeklyByProjectVo> weeklyByProjectList(@Param("id") String id,  @Param("name") String name);
}
