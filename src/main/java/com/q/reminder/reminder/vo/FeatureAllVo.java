package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.FeatureAllVo
 * @Description :
 * @date :  2023/7/25 16:00
 */
@Data
public class FeatureAllVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -3543942571421313391L;
    private String label;
    private String value;
}
