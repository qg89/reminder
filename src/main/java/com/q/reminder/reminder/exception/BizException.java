package com.q.reminder.reminder.exception;

import com.q.reminder.reminder.enums.BizResultCode;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.exception.BizException
 * @Description :
 * @date :  2023.03.27 10:47
 */
public class BizException extends RuntimeException{
    /**
     * 结果码
     */
    private String code;

    /**
     * 结果码信息
     */
    private String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    /**
     * 构造函数
     *
     * @param resultCode
     */
    public BizException(BizResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码对象
     * @param cause      异常
     */
    public BizException(BizResultCode resultCode, Throwable cause) {
        super(cause);
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    /**
     * 构造函数
     *
     * @param resultCode    错误码对象
     * @param detailMessage 错误详细信息
     */
    public BizException(BizResultCode resultCode, String detailMessage) {
        super(detailMessage);
        this.code = resultCode.getCode();
        this.msg = detailMessage;
    }

    /**
     * 构造函数
     *
     * @param resultCode    错误码对象
     * @param detailMessage 错误详细信息
     * @param cause         异常
     */
    public BizException(BizResultCode resultCode, String detailMessage, Throwable cause) {
        super(detailMessage, cause);
        this.code = resultCode.getCode();
        this.msg = detailMessage;
    }

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param msg  错误描述
     */
    public BizException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造函数
     *
     * @param code  错误码
     * @param msg   错误描述
     * @param cause 异常
     */
    public BizException(String code, String msg, Throwable cause) {
        super(cause);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造函数
     *
     * @param code          错误码
     * @param msg           错误描述
     * @param detailMessage 错误详细信息
     */
    public BizException(String code, String msg, String detailMessage) {
        super(detailMessage);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造函数
     *
     * @param code          错误码
     * @param msg           错误描述
     * @param detailMessage 错误详细信息
     * @param cause         异常对象
     */
    public BizException(String code, String msg, String detailMessage, Throwable cause) {
        super(detailMessage, cause);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 业务异常，错误码默认为1
     *
     * @param msg 错误描述
     */
    public BizException(String msg) {
        super(msg);
        this.code = BizResultCode.ERR_SYSTEM.getCode();
        this.msg = msg;
    }

    /**
     * 业务异常，错误码默认为1
     *
     * @param msg 错误描述
     */
    public BizException(String msg, Throwable cause) {
        super(cause);
        this.code = BizResultCode.ERR_SYSTEM.getCode();
        this.msg = msg;
    }
}
