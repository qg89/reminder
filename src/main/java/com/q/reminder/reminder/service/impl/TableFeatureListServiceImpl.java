package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.TableFeatureList;
import com.q.reminder.reminder.mapper.TableFeatureListMapping;
import com.q.reminder.reminder.service.TableFeatureListService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 需求管理表列表(TableFeatureList)表服务实现类
 *
 * @author makejava
 * @since 2023-10-24 11:26:16
 */
@Service
public class TableFeatureListServiceImpl extends ServiceImpl<TableFeatureListMapping, TableFeatureList> implements TableFeatureListService {

    @Override
    public List<TableFeatureList> listToDo() {
        LambdaQueryWrapper<TableFeatureList> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TableFeatureList::getFeatureState, "待开发")
                .isNull(TableFeatureList::getRedmineId);
        return baseMapper.selectList(wrapper);
    }
}
