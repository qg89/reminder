package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.UserProject
 * @Description :
 * @date :  2022.09.23 14:52
 */
@Data
public class UserProjectVo implements Serializable {
    private static final long serialVersionUID = 8105400383256294587L;
    private String userId;
    private String userName;
    private String pId;
}
