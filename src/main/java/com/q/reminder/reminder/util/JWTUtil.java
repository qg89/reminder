package com.q.reminder.reminder.util;

import com.q.reminder.reminder.util.jjwtutil.JJWTUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.JWTUtil
 * @Description :
 * @date :  2022.11.17 12:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Configuration
@Component
public class JWTUtil extends JJWTUtil {
}
