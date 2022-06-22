package com.seu.activitis.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.activitis.service.ProcessBusinessService;
import com.seu.util.entity.ProcessBusiness;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/9/6 12:42
 */
@Api(value = "流程业务保存")
@RestController
@RequestMapping("/business")
public class ProcessBusinessController {

    @Resource
    ProcessBusinessService processBusinessService;

    @PostMapping
    public ResultVo save (String data) {
        ProcessBusiness processBusiness = JSON.toJavaObject(JSON.parseObject(data), ProcessBusiness.class);
        return processBusinessService.save(processBusiness)? new ResultVo(ResultCode.SUCCESS,processBusiness.getId()):new ResultVo(ResultCode.FAIL);
    }

    @GetMapping
    public ResultVo get (String data) {
        JSONObject params= JSON.parseObject(data);
        QueryWrapper<ProcessBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user", params.get("user"));
        return new ResultVo(ResultCode.SUCCESS,processBusinessService.list(queryWrapper));
    }

    @DeleteMapping
    public ResultVo del (String data) {
        String id = data.replace("\"", "");
        return processBusinessService.removeById(id) ? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }
}
