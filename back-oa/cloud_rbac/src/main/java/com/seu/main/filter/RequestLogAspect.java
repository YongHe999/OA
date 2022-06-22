package com.seu.main.filter;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/6/10 17:14
 * @description
 */
@Component
@Aspect
@Slf4j
public class RequestLogAspect {

    /**
     * mainController包的切入点
     */
    @Pointcut("execution(public * com.seu.main.controller..*.*(..))")
    public void mainControllerRequestLog(){}
    /**
     * cusController包的切入点
     */
//    @Pointcut("execution(public * com.seu.cus.controller..*.*(..))")
//    public void cusControllerRequestLog(){}

    /**
     * 在切入点的方法run之前要干的
     * @param joinPoint
     */
//    @Before("mainControllerRequestLog()||cusControllerRequestLog()")
    @Before("mainControllerRequestLog()")
    public void logBeforeController(JoinPoint joinPoint) {

        //RequestContextHolder是Springmvc提供来获得请求的东西
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        // 记录请求内容
        log.info("请求地址 : " + request.getRequestURL().toString());
        log.info("请求方式 : " + request.getMethod());
        log.info("IP : " + request.getRemoteAddr());
        log.info("参数 : " + Arrays.toString(joinPoint.getArgs()));
        //下面这个getSignature().getDeclaringTypeName()是获取包+类名的   然后后面的joinPoint.getSignature.getName()获取了方法名
        log.info("方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        //logger.info("################TARGET: " + joinPoint.getTarget());//返回的是需要加强的目标类的对象
        //logger.info("################THIS: " + joinPoint.getThis());//返回的是经过加强后的代理类的对象

    }
}
