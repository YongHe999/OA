package com.seu.main.scheduled;

import com.seu.main.service.LogService;
import com.seu.main.service.TokenService;
import com.seu.util.entity.Token;
import com.seu.util.sysEnum.LoggerFlagEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/5/28 16:28
 * @description 定时器
 */
@Configuration
@EnableScheduling
@EnableAsync
@Slf4j
public class ClearTokenSchedul {
    @Autowired
    TokenService tokenService;
    @Autowired
    LogService logService;
    @Async
    @Scheduled(cron = "0 0 2 * * ?")  //每周星期六凌晨1点实行一次：0 0 1 ? * L
    public void first() throws InterruptedException {
        /**
         * 获取所有token
         */
        List<Token> tokenList=tokenService.listTokens();
        for (Token token : tokenList) {
            //清空过期的token
            if (token.getExpireTime().getTime() < System.currentTimeMillis()){
                tokenService.deleteTokenById(token.getId());
            }
        }
        logService.save("清空无效token", LoggerFlagEnum.NORMAL.getValue(),"定时清空无效token :"+LocalDateTime.now().toLocalTime()+" ,线程 :"+Thread.currentThread().getName());
    }
}
