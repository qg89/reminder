package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.constant.WmonthConstants;
import com.q.reminder.reminder.entity.WRole;
import com.q.reminder.reminder.mapper.WRoleMapping;
import com.q.reminder.reminder.service.WRoleService;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色(WRole)表服务实现类
 *
 * @author makejava
 * @since 2022-12-29 09:16:15
 */
@Service
public class WRoleServiceImpl extends ServiceImpl<WRoleMapping, WRole> implements WRoleService {

    @Override
    public List<RoleInvolvementVo> roleInvolvement(String pId, String year) {
        List<RoleInvolvementVo> voList = baseMapper.roleInvolvement(pId, year);
        List<RoleInvolvementVo> data = new ArrayList<>();
        voList.stream().collect(Collectors.groupingBy(RoleInvolvementVo::getMonths)).forEach((m, l) -> {
            for (RoleInvolvementVo v : l) {
                RoleInvolvementVo vo = new RoleInvolvementVo();
                vo.setName(v.getName());
                String hours = v.getHours();
                switch (m) {
                    case WmonthConstants.JAN -> vo.setJan(hours);
                    case WmonthConstants.FEB -> vo.setFeb(hours);
                    case WmonthConstants.MAR -> vo.setMar(hours);
                    case WmonthConstants.ARP -> vo.setArp(hours);
                    case WmonthConstants.MAY -> vo.setMay(hours);
                    case WmonthConstants.JUN -> vo.setJun(hours);
                    case WmonthConstants.JUL -> vo.setJul(hours);
                    case WmonthConstants.AUG -> vo.setAug(hours);
                    case WmonthConstants.SEP -> vo.setSep(hours);
                    case WmonthConstants.OCT -> vo.setOct(hours);
                    case WmonthConstants.NOV -> vo.setNov(hours);
                    case WmonthConstants.DEC -> vo.setDec(hours);
                    default -> {}
                }
                data.add(vo);
            }
        });
        return data;
    }
}
