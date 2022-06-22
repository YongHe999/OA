package com.seu;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;

/**
 * @author Ajie
 */

//排除activiti自带的security，使用自己编写的安全框架
@SpringBootApplication(exclude = {org.activiti.spring.boot.SecurityAutoConfiguration.class,org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@EnableTransactionManagement
@MapperScan(basePackages = "com.seu.activitis.mapper")
@Slf4j
@EnableDiscoveryClient
public class ActivitiApplication {
    //上下文对象
    private static ApplicationContext applicationContext;
    //程序主入口
    public static void main(String[] args) {

        applicationContext=SpringApplication.run(ActivitiApplication.class, args);
        //服务器对象
        TomcatServletWebServerFactory tomcatServletWebServerFactory= (TomcatServletWebServerFactory) applicationContext.getBean("tomcatServletWebServerFactory");
        try {
            //ip地址
            String host= InetAddress.getLocalHost().getHostAddress();
            //端口
            int port=tomcatServletWebServerFactory.getPort();
            //项目名称（url前缀）
            String contextPath = tomcatServletWebServerFactory.getContextPath();
            log.info("应用访问地址：{}:{}{}",host,port,contextPath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
