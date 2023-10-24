package com.q.reminder.reminder.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.TableFeatureRole;
import com.q.reminder.reminder.mapper.TableFeatureRoleMapping;
import com.q.reminder.reminder.service.TableFeatureRoleService;
import org.springframework.stereotype.Service;


/**
 * 需求管理表各角色列表(TableFeatureRole)表服务实现类
 *
 * @author makejava
 * @since 2023-10-24 12:25:00
 */
@Service
public class TableFeatureRoleServiceImpl extends MppServiceImpl<TableFeatureRoleMapping, TableFeatureRole> implements TableFeatureRoleService {

}
