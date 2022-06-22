package com.seu.util.sysEnum;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/6/11 14:49
 * @description 用户状态标志
 */
public enum UserStatusEnum {
    //禁用
    DISABLED(0),
    //可用
    VALID(1),
    //锁定
    LOCKED(2);

    private final int status;

    UserStatusEnum(int status){
        this.status=status;
    }
    public Integer getValue(){
        return this.status;
    }

}
