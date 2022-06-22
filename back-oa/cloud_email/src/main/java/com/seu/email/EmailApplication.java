package com.seu.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;

@SpringBootApplication
@Slf4j
@EnableDiscoveryClient
public class EmailApplication {

    //上下文对象
    private static ApplicationContext applicationContext;
    //程序主入口
    public static void main(String[] args) {

        applicationContext = SpringApplication.run(EmailApplication.class, args);
        //服务器对象
        TomcatServletWebServerFactory tomcatServletWebServerFactory = (TomcatServletWebServerFactory) applicationContext.getBean("tomcatServletWebServerFactory");
        try {
            //ip地址
            String host = InetAddress.getLocalHost().getHostAddress();
            //端口
            int port = tomcatServletWebServerFactory.getPort();
            //项目名称（url前缀）
            String contextPath = tomcatServletWebServerFactory.getContextPath();
            log.info("应用访问地址：{}:{}{}", host, port, contextPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
