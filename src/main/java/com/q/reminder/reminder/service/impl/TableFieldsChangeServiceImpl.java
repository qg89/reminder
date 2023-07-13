package com.q.reminder.reminder.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.TableFieldsChange;
import com.q.reminder.reminder.mapper.TableFieldsChangeMapping;
import com.q.reminder.reminder.service.TableFieldsChangeService;
import org.springframework.stereotype.Service;


/**
 * 多维表格，表字段(TableFieldsFeature)表服务实现类
 *
 * @author makejava
 * @since 2023-07-13 14:33:51
 */
@Service
public class TableFieldsChangeServiceImpl extends MppServiceImpl<TableFieldsChangeMapping, TableFieldsChange> implements TableFieldsChangeService {
    
}
