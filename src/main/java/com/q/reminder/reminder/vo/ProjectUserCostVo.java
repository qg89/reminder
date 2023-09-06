package com.q.reminder.reminder.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ProjectUserCostVo
 * @Description :
 * @date :  2023.09.06 13:17
 */
@Data
public class ProjectUserCostVo extends ProjectCostBaseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8259323546038050039L;

    @JSONField(serialize = false)
    private String userName;
    private String spentOn;
}
