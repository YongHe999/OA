package com.seu.main.config.security;


import com.seu.main.dto.ResponseInfo;
import com.seu.util.sysEnum.ResultCode;
import com.seu.util.utils.ResponseUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 *
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/12/7 19:19
 */
@RestControllerAdvice
public class accessDeniedExceptionHandler {
    // 会执行该方法
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException e, HttpServletResponse response){
        ResponseInfo info = new ResponseInfo(ResultCode.UNAUTHORISE.getCode(), "权限不足！");

        ResponseUtil.responseJson(response, ResultCode.UNAUTHORISE.getCode(), info);
    }

}
