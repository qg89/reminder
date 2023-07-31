package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ProjectInfoVo
 * @Description :
 * @date :  2023.03.06 10:59
 */
@Data
public class ProjectInfoVo<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4476290887595518857L;

    private String key;
    private String label;
    private T value;
    private String columnType;
    private Object columnDesc;
    private Integer showEdit;
}
