package com.q.reminder.reminder.vo;

import com.q.reminder.reminder.ano.Format;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ProjectCostVo
 * @Description :
 * @date :  2023/8/10 10:09
 */
@Data
public class ProjectCostVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 8862486811349444728L;

   private String pid;

   private String copq;

    /**
     * 项目预算
     */
    @Format(2)
   private Double budget;

    /**
     * 目前成本
     */
    @Format(2)
   private Double cost;

    /**
     * 目前成本（利润）
     */
    @Format(2)
   private Double costProfit;

    /**
     * 人力合计（小时）
     */
    @Format(2)
   private Double peopleHours;

    /**
     * 人力合计（月）
     */
    @Format(2)
   private Double peopleMonth;
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

    private String months;

    private String shortName;
}
