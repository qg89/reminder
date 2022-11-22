package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.BaseFeishuVo
 * @Description :
 * @date :  2022.11.03 14:19
 */
@Data
public class BaseFeishuVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1592713460943510102L;
    private String appId;
    private String appSecret;
}
