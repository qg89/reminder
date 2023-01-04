package com.q.reminder.reminder.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.RoleInvolvementVo
 * @Description :
 * @date :  2022.12.29 09:19
 */
@Data
public class RoleInvolvementVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -302010094843565788L;

    private String name;
    private String group;
    private String role;
    @JSONField(serialize = false)
    private String hours;
    @JSONField(serialize = false)
    private String months;
    @JSONField(serialize = false)
    private Integer sort;
    private String jan;
    private String feb;
    private String mar;
    private String arp;
    private String may;
    private String jun;
    private String jul;
    private String aug;
    private String sep;
    private String oct;
    private String nov;
    private String dec;
}
