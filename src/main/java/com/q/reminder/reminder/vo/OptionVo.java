package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.OptionVo
 * @Description :
 * @date :  2022.12.30 14:36
 */
@Data
public class OptionVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 7482278041918645547L;

    private String id;
    private String pId;
    private String name;
    List<OptionVo> child;

}
