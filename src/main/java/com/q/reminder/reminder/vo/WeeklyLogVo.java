package com.q.reminder.reminder.vo;

import lombok.Data;
import org.apache.logging.log4j.Logger;
import tech.powerjob.worker.log.OmsLogger;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.WeeklyLogVo
 * @Description :
 * @date :  2023/5/15 12:59
 */
@Data
public class WeeklyLogVo<L extends Logger, V extends OmsLogger> {

    private Logger logger;
    private OmsLogger omsLogger;

    public WeeklyLogVo(OmsLogger omsLogger) {
        this.omsLogger = omsLogger;
    }

    public WeeklyLogVo(Logger logger) {
        this.logger = logger;
    }
}
