package com.q.reminder.reminder.cpp;

import com.sun.jna.Library;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.Demo
 * @Description :
 * @date :  2023.10.12 15:17
 */
public class Demo123 implements Library {

    public native String sayHello(String publicKeyStr);
}
