package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.WRole;
import com.q.reminder.reminder.mapper.WRoleMapping;
import com.q.reminder.reminder.service.WRoleService;
import com.q.reminder.reminder.util.RoleInvolvementUtils;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色(WRole)表服务实现类
 *
 * @author makejava
 * @since 2022-12-29 09:16:15
 */
@Service
@RequiredArgsConstructor
public class WRoleServiceImpl extends ServiceImpl<WRoleMapping, WRole> implements WRoleService {
    private final WRoleService wRoleService;

    @Override
    public List<RoleInvolvementVo> roleInvolvement(WorkloadParamsVo params) {
        List<RoleInvolvementVo> voList = baseMapper.roleInvolvement(params);
        List<RoleInvolvementVo> vos = RoleInvolvementUtils.getRoleInvolvementVos(voList).stream().sorted(Comparator.comparing(RoleInvolvementVo::getSort)).toList();
        Map<String, List<RoleInvolvementVo>> map = vos.stream().collect(Collectors.groupingBy(RoleInvolvementVo::getName));
        List<String> list = wRoleService.list().stream().map(WRole::getRole).toList();
        vos = new ArrayList<>();
        List<String> roleList = new ArrayList<>(list);
        roleList.add("合计");
        for (String role : roleList) {
            List<RoleInvolvementVo> vl = map.get(role);
            if (!CollectionUtils.isEmpty(vl)) {
                vos.addAll(vl);
            } else {
                RoleInvolvementVo vo = new RoleInvolvementVo();
                vo.setName(role);
                vo.setJan("0.00");
                vo.setFeb("0.00");
                vo.setMar("0.00");
                vo.setArp("0.00");
                vo.setMay("0.00");
                vo.setJun("0.00");
                vo.setJul("0.00");
                vo.setAug("0.00");
                vo.setOct("0.00");
                vo.setSep("0.00");
                vo.setNov("0.00");
                vo.setDec("0.00");
                vos.add(vo);
            }
        }
        return vos;
    }
}

