package com.q.reminder.reminder.util;

import com.q.reminder.reminder.constant.WmonthConstants;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.RoleInvolvementUtils
 * @Description :
 * @date :  2022.12.29 14:16
 */
public class RoleInvolvementUtils {

    @NotNull
    public static List<RoleInvolvementVo> getRoleInvolvementVos(List<RoleInvolvementVo> voList) {
        List<RoleInvolvementVo> data = new ArrayList<>();
        voList.stream().collect(Collectors.groupingBy(RoleInvolvementVo::getName)).forEach((name, l) -> {
            RoleInvolvementVo vo = new RoleInvolvementVo();
            vo.setName(name);
            vo.setJan("0.00");
            vo.setFeb("0.00");
            vo.setMar("0.00");
            vo.setJun("0.00");
            vo.setMay("0.00");
            vo.setJul("0.00");
            vo.setAug("0.00");
            vo.setSep("0.00");
            vo.setOct("0.00");
            vo.setSep("0.00");
            vo.setNov("0.00");
            vo.setDec("0.00");
            for (RoleInvolvementVo v : l) {
                String months = v.getMonths();
                String hours = v.getHours();
                switch (months) {
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
            }
            data.add(vo);
        });
        return data;
    }
}
