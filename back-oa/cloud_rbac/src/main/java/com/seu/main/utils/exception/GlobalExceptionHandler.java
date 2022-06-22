package com.seu.main.utils.exception;

import com.seu.util.entity.vo.ResultVo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/6/23 11:26
 * @description 404 500异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResultVo defaultErrorHandler(HttpServletRequest request, Exception e){
        if(e instanceof NoHandlerFoundException){
            // 404
            return new ResultVo(HttpStatus.NOT_FOUND.value(),e.getMessage());
        }
        else {
            // 500
//            return null;
//            System.out.println("1111");
            return new ResultVo(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
        }
    }
}
