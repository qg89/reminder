package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.RedmineNoneTimeVo
 * @Description :
 * @date :  2023.11.14 13:58
 */
@Data
public class RedmineNoneTimeVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 5066102270415906886L;
    private Integer userId;
    private String userName;
    private Date spentOn;
    private Double hours;
}
