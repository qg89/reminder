package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ProjectInfoVo
 * @Description :
 * @date :  2023.03.06 10:59
 */
@Data
public class ProjectInfoVo implements Serializable {

    private String key;
    private String label;
    private Object value;
    private String columnType;
    private Object columnDesc;
}
