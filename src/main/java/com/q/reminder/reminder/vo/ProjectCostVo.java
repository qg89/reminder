package com.q.reminder.reminder.vo;

import com.alibaba.fastjson2.annotation.JSONField;
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
public class ProjectCostVo extends ProjectCostBaseVo implements Serializable {


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
     * 人力合计（月）
     */
    @Format(2)
   private Double peopleMonth;

    @JSONField(serialize = false)
    private Double proportion;
}
