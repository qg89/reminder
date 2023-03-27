package com.q.reminder.reminder.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ContentVo
 * @Description :
 * @date :  2022.11.16 13:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentVo extends MessageVo implements Serializable {
    private String receiveId;
    private String content;
    private String msgType;
    private String parentNode;
    private File file;
    private String fileType;
}
