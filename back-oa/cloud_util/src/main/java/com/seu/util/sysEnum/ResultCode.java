package com.seu.util.sysEnum;

public enum ResultCode {
    //操作成功
    SUCCESS(200,"操作成功！"),
    //操作失败
    SUCCESS_LAYUI(0,"操作成功！"),

    NOEXSISTID(501,"id不能为空"),
    FAIL(-1,"操作失败！"),
    NULLFAIL(-1,"必要参数为空！"),
    UNAUTHORISE(401,"权限不足！"),
    UNAUTHENTICATED(402,"请先登录！"),
    EXIT(10004,"该对象已存在！"),
    HAVINGCHILD(10005,"存在子节点，无法删除。"),
    USERNAME_ERROR(601,"抱歉，用户名不存在！"),
    PASSWORD_ERROR(602,"抱歉，密码错误！"),
    SERVER_ERROR(99999,"抱歉，系统繁忙，请稍后再试！");
    //状态编码
    int code;
    //结果信息
    String message;

    ResultCode(int code, String message){
        this.code=code;
        this.message=message;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
