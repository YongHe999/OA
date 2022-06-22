package com.seu.chat.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 * TODO
 * websocket 配置,开启webSocket支持
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/9/3 19:07
 */
//Configuration用于定义配置类，可替换xml配置文件，被注解的类内部包含有一个或多个被@Bean注解的方法
@Configuration
public class websocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
