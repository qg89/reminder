package com.q.reminder.reminder.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.FeishuUploadImageVo
 * @Description :
 * @date :  2022.11.03 14:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FeishuUploadImageVo extends WeeklyProjectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5026677480686131016L;
    private Long size;
    private String parentNode;
    private String parentType;
    private File file;
}
