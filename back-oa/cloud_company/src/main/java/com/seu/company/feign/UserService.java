package com.seu.company.feign;

import com.seu.util.entity.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * TODO
 *
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/10/23 11:53
 */

@Api("调用rbac服务")
@FeignClient(value = "cloud-rbac")//注册中心中显示的微服务名称
public interface UserService {
    @ApiOperation("获取登录用户")
    @GetMapping(value = "/user/info")
    ResultVo getUser(@RequestHeader("token") String token);
}
