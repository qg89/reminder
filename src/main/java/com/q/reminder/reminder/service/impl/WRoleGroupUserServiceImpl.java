package com.q.reminder.reminder.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.WRoleGroupUser;
import com.q.reminder.reminder.mapper.WRoleGroupUserMapping;
import com.q.reminder.reminder.service.WRoleGroupUserService;
import com.q.reminder.reminder.vo.GroupRoleUserIdsVo;
import com.q.reminder.reminder.vo.OptionVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * (WRoleGroupUser)表服务实现类
 *
 * @author makejava
 * @since 2022-12-28 10:30:54
 */
@Service
public class WRoleGroupUserServiceImpl extends MppServiceImpl<WRoleGroupUserMapping, WRoleGroupUser> implements WRoleGroupUserService {


    @Override
    public List<OptionVo> option(WorkloadParamsVo paramsVo) {
        List<String> gList = paramsVo.getGroupId();
        List<String> rList = paramsVo.getRoleId();
        List<String> uList = paramsVo.getUserId();
        List<OptionVo> result = new ArrayList<>();
        List<GroupRoleUserIdsVo> options = baseMapper.option();
        options.stream().filter(e -> {
            boolean re = true;
            if (!CollectionUtils.isEmpty(gList)) {
                re = gList.contains(e.getGroupId());
            }
            if (!CollectionUtils.isEmpty(rList)) {
                re = rList.contains(e.getRoleId());
            }
            if (!CollectionUtils.isEmpty(uList)) {
                re = uList.contains(e.getUserId());
            }
            return re;
        }).collect(Collectors.groupingBy(GroupRoleUserIdsVo::getGroupId, Collectors.groupingBy(GroupRoleUserIdsVo::getRoleId, Collectors.toMap(GroupRoleUserIdsVo::getUserId, Function.identity(), (v1, v2) -> v1)))).forEach((gk, gv) -> {
            OptionVo gVo = new OptionVo();
            gVo.setId(gk);
            gVo.setPId("0");
            List<OptionVo> rl = new ArrayList<>();
            gv.forEach((rk, rv) -> {
                OptionVo rVo = new OptionVo();
                rVo.setId(rk);
                rVo.setPId(gk);
                List<OptionVo> ul = new ArrayList<>();
                rv.forEach((id, info) -> {
                    OptionVo uVo = new OptionVo();
                    uVo.setId(id);
                    uVo.setName(info.getUserName());
                    uVo.setPId(rk);
                    ul.add(uVo);
                    rVo.setName(info.getRoleName());
                    gVo.setName(info.getGroupName());
                });
                rVo.setChild(ul);
                rl.add(rVo);
            });
            gVo.setChild(rl);
            result.add(gVo);
        });
        return result;
    }
}
