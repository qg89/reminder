package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.mapper.TTableInfoMapping;
import com.q.reminder.reminder.service.TTableInfoService;
import com.q.reminder.reminder.vo.table.FeatureVo;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * (TTableInfo)表服务实现类
 *
 * @author makejava
 * @since 2023-01-18 13:31:18
 */
@Service
public class TTableInfoServiceImpl extends ServiceImpl<TTableInfoMapping, TTableInfo> implements TTableInfoService {

    @Override
    public List<FeatureVo> listByTableType(String featureTmp) {
        return baseMapper.listByTableType(featureTmp);
    }
}
