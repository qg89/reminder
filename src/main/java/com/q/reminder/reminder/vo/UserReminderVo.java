package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.UserRedminderVo
 * @Description :
 * @date :  2023.09.06 16:14
 */
@Data
public class UserReminderVo implements Serializable {

    private String memberId;
    private String userName;
    private String userId;
    private Date startDate;
    private Date endDate;
    private String enable;
    private Double hours;
    private String spentOn;
}
