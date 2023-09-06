package com.q.reminder.reminder.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.q.reminder.reminder.ano.Format;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ProjectCostBaseVo
 * @Description :
 * @date :  2023.09.06 13:22
 */
@Data
public class ProjectCostBaseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -5486550838768396051L;
    @JSONField(serialize = false)
    private String userDate;
    /**
     * 人力合计（小时）
     */
    @Format(2)
    private Double peopleHours;
    /**
     * 加班合计（小时）
     */
    @Format(2)
    private Double overtime;
    /**
     * 正常合计（小时）
     */
    @Format(2)
    private Double normal;

    private String shortName;
}
