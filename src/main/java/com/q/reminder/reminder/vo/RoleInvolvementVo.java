package com.q.reminder.reminder.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.q.reminder.reminder.ano.Format;
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

    private String id;
    private String name;
    private String group;
    private String role;
    @JSONField(serialize = false)
    private String hours;
    @JSONField(serialize = false)
    private String months;
    @JSONField(serialize = false)
    private Integer sort;
    @Format(2)
    private String jan;
    @Format(2)
    private String feb;
    @Format(2)
    private String mar;
    @Format(2)
    private String arp;
    @Format(2)
    private String may;
    @Format(2)
    private String jun;
    @Format(2)
    private String jul;
    @Format(2)
    private String aug;
    @Format(2)
    private String sep;
    @Format(2)
    private String oct;
    @Format(2)
    private String nov;
    @Format(2)
    private String dec;
}
