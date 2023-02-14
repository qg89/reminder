package com.q.reminder.reminder.vo.table;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.table.ColumnEntityVo
 * @Description :
 * @date :  2023.01.31 16:48
 */
@Data
public class ColumnEntityVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1674434289733667996L;
    private String columnName;
    private String entity;
    private String tableId;
}
