package com.q.reminder.reminder.util.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 统计信息表
 * @Author: xiaobing
 * @system name: 工作流引擎
 * @copyright：长安新生（深圳）金融投资有限公司
 * @Date: Created in  2018/9/26 13:34
 */
@Data
public class GitCount implements Serializable {

    @Serial
    private static final long serialVersionUID = -6850038631408044827L;
    private String emailAddress;
    private String authName;
    private String commitDate;
    private String commitMessage;
    private int addLine;
    private int removeLine;
    private String projectCde;
    private String isMerge;
    private String branch;
}
