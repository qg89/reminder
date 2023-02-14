package com.q.reminder.reminder.util;

import com.q.reminder.reminder.constant.WmonthConstants;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            vo.setArp("0.00");
            vo.setMay("0.00");
            vo.setJun("0.00");
            vo.setJul("0.00");
            vo.setAug("0.00");
            vo.setOct("0.00");
            vo.setSep("0.00");
            vo.setNov("0.00");
            vo.setDec("0.00");
            String id = null;
            Integer sort = null;
            for (RoleInvolvementVo v : l) {
                String months = v.getMonths();
                String hours = v.getHours();
                sort = v.getSort();
                id = v.getId();
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
            vo.setId(id);
            vo.setSort(sort);
            data.add(vo);
        });
        Double janHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getJan())).sum();
        Double febHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getFeb())).sum();
        Double marHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getMar())).sum();
        Double arpHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getArp())).sum();
        Double mayHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getMay())).sum();
        Double junHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getJun())).sum();
        Double julHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getJul())).sum();
        Double augHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getAug())).sum();
        Double sepHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getSep())).sum();
        Double octHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getOct())).sum();
        Double novHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getNov())).sum();
        Double decHourse = data.stream().mapToDouble(e -> Double.parseDouble(e.getDec())).sum();
        RoleInvolvementVo all = new RoleInvolvementVo();
        all.setName("合计");
        all.setJan(String.valueOf(janHourse));
        all.setFeb(String.valueOf(febHourse));
        all.setMar(String.valueOf(marHourse));
        all.setArp(String.valueOf(arpHourse));
        all.setMay(String.valueOf(mayHourse));
        all.setJun(String.valueOf(junHourse));
        all.setJul(String.valueOf(julHourse));
        all.setAug(String.valueOf(augHourse));
        all.setSep(String.valueOf(sepHourse));
        all.setOct(String.valueOf(octHourse));
        all.setNov(String.valueOf(novHourse));
        all.setDec(String.valueOf(decHourse));
        all.setSort(999);
        data.add(all);
        return data;
    }
}
