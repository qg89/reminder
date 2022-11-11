package com.q.reminder.reminder.handle;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.SaikoException
 * @Description :
 * @date :  2022.11.11 14:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SaikoException extends Exception implements Serializable {

    @Serial
    private static final long serialVersionUID = 6034169856402023995L;
    private String msg;

    public SaikoException(Exception e) {
        super(e);
    }
}
