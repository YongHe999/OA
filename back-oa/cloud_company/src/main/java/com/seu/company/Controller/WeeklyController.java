package com.seu.company.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seu.company.Service.WeeklyService;
import com.seu.util.entity.Weekly;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-11
 */
@RestController
@RequestMapping("/weekly")
public class WeeklyController {
    @Resource
    private WeeklyService weeklyService;

    @GetMapping
    public ResultVo getWeekly(String data) {
        if (data.isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        JSONObject params= JSON.parseObject(data);
        return weeklyService.WeeklyPageList(params,new Page<>(params.getInteger("page"), params.getInteger("limit"),true));
    }

    @PostMapping
    public ResultVo addWeekly(String data) {
        Weekly weekly = JSON.toJavaObject(JSON.parseObject(data),Weekly.class);
        return weeklyService.save(weekly)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }
    @PutMapping
    public ResultVo updateWeekly(String data) {
        Weekly weekly = JSON.toJavaObject(JSON.parseObject(data),Weekly.class);
        return weeklyService.updateById(weekly)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @DeleteMapping
    public ResultVo delWeekly(String data) {
        return weeklyService.removeById(data.replace("\"",""))? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }
}

