package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.Coverity;
import com.q.reminder.reminder.mapper.CoverityMapping;
import com.q.reminder.reminder.service.CoverityService;
import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.CoverityServiceImpl
 * @Description :
 * @date :  2022.12.01 10:36
 */
@Service
public class CoverityServiceImpl extends ServiceImpl<CoverityMapping, Coverity> implements CoverityService {
    @Override
    public List<CoverityAndRedmineSaveTaskVo> queryByProject(String pKey) {
        return baseMapper.queryByProject(pKey);
    }
}
