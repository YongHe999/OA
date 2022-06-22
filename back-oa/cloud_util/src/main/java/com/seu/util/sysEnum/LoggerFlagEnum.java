package com.seu.util.sysEnum;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/6/11 14:49
 * @description 日志状态标志 1 正常 0 异常
 */
public enum LoggerFlagEnum {
    //正常
    NORMAL(1),
    //异常
    ABNORMAL(0);

    private final int code;

    LoggerFlagEnum(int code){
        this.code=code;
    }
    public Integer getValue(){
        return this.code;
    }

}
