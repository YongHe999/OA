package com.seu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;

/**
 * @author Ajie
 */
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
//@EnableDiscoveryClient
@EnableTransactionManagement
@Slf4j
public class GatewayApplication {

    @Value("${test.a}")
    private static int port;
//    @Value("${test.path}")
//    private static String contextPath;

    //上下文对象
    private static ApplicationContext applicationContext;
    //程序主入口
    public static void main(String[] args) {
        applicationContext=SpringApplication.run(GatewayApplication.class, args);
        //服务器对象
//        TomcatServletWebServerFactory tomcatServletWebServerFactory= (TomcatServletWebServerFactory) applicationContext.getBean("tomcatServletWebServerFactory");
        try {
            //ip地址
            String host= InetAddress.getLocalHost().getHostAddress();
            //端口
//            int port=tomcatServletWebServerFactory.getPort();
            //项目名称（url前缀）
//            String contextPath = tomcatServletWebServerFactory.getContextPath();
            log.info("应用访问地址：{}:{}",host,port);
//            log.info("应用访问地址：{}:{}{}",host);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
