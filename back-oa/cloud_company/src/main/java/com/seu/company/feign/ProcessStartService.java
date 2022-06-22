package com.seu.company.feign;

import com.seu.util.entity.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * TODO 启动流程
 *
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/10/9 12:52
 */

@Service
@Api("调用流程服务")
@FeignClient(value = "cloud-activiti")//注册中心中显示的微服务名称
public interface ProcessStartService {

    @ApiOperation("获得流程app")
    @GetMapping("/processapp/getapp")
    ResultVo getApp(String data);

    @ApiOperation("启动流程")
    @PostMapping("/task/startprocess")
    ResultVo startProcess(String data);
}
