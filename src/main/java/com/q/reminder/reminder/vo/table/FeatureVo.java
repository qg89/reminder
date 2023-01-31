package com.q.reminder.reminder.vo.table;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.table.FeatureVo
 * @Description :
 * @date :  2023.01.30 15:55
 */
@Data
public class FeatureVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1072496721024736890L;

    private String appToken;
    private String tableId;
    private String viewId;
    private String columnName;
}
