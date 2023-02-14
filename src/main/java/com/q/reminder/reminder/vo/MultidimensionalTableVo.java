package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.MultidimensionalTableVo
 * @Description :
 * @date :  2023.01.18 11:31
 */
@Data
public class MultidimensionalTableVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5929928279342239347L;

    private String appToken;
    private String tableId;
    private String viewId;
}
