package com.seu.util.entity.vo;

import com.seu.util.sysEnum.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一返回类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private Integer code;
    private String message;
    private Object data;

    public ResultVo(ResultCode resultCode){
        this.code=resultCode.getCode();
        this.message=resultCode.getMessage();
    }
    public ResultVo(ResultCode resultCode, Object data){
        this.code=resultCode.getCode();
        this.message=resultCode.getMessage();
        this.data=data;
    }

    public ResultVo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }





}
