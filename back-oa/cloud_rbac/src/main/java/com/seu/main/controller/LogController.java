package com.seu.main.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.seu.main.service.LogService;
import com.seu.util.entity.SysLogs;
import com.seu.util.entity.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/2/28 16:38
 * @description
 */
@Api(tags = "日志相关接口")
@RestController
@RequestMapping("/syslogs")
public class LogController {
    @Autowired
    LogService logService;

    @GetMapping
    @ApiOperation("获取日志分页列表")
    public ResultVo pageListLogs(String data){
        JSONObject params=JSON.parseObject(data);
        return logService.pageListLogs(params,new Page<SysLogs>((int) params.get("page"), (int) params.get("limit"),true));
    }

    @ApiOperation("批量删除日志")
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys_log_delete')")
    public ResultVo delLogsByIds(String data){
        JSONObject jo=JSONObject.parseObject(data);
        List<String> logIds= JSON.parseArray(jo.getString("ids"),String.class);
        return logService.delLogsByIds(logIds);
    }
}
