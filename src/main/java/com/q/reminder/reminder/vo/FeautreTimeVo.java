package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.FeautreTimeVo
 * @Description :
 * @date :  2023.03.24 16:33
 */
@Data
public class FeautreTimeVo implements Serializable {


    private Integer id;
    private String name;
    private Float times;
    private String recordsId;
}
