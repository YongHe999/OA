package com.seu.util.sysEnum;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/6/11 14:49
 * @description 用户状态标志
 */
public enum PermissionTypeEnum {
    //禁用
    MENU(1),
    //可用
    BUTTON(2);

    private final int status;

    PermissionTypeEnum(int status){
        this.status=status;
    }
    public Integer getValue(){
        return this.status;
    }

}
