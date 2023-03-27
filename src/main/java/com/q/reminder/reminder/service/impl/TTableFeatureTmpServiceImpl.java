package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.mapper.TTableFeatureTmpMapping;
import com.q.reminder.reminder.service.TTableFeatureTmpService;
import com.q.reminder.reminder.vo.FeautreTimeVo;
import com.q.reminder.reminder.vo.RedmineDataVo;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 需求管理表临时表（避免重复）(TTableFeatureTmp)表服务实现类
 *
 * @author makejava
 * @since 2023-02-01 17:36:49
 */
@Service
public class TTableFeatureTmpServiceImpl extends ServiceImpl<TTableFeatureTmpMapping, TTableFeatureTmp> implements TTableFeatureTmpService {


    @Override
    public List<FeautreTimeVo> queryAllTimes() {
        return baseMapper.queryAllTimes();
    }

    @Override
    public List<RedmineDataVo> listByProject() {
        return baseMapper.listByProject();
    }
}
