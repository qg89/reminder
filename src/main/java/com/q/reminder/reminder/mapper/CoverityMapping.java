package com.q.reminder.reminder.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.q.reminder.reminder.entity.Coverity;
import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.mapper.CoverityMapping
 * @Description :
 * @date :  2022.12.01 10:35
 */
@Mapper
public interface CoverityMapping extends MPJBaseMapper<Coverity> {
    List<CoverityAndRedmineSaveTaskVo> queryByProject(@Param("pKey") String pKey);

}
