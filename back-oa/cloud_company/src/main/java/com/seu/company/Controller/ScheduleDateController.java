package com.seu.company.Controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.company.Service.ScheduleDateService;
import com.seu.util.entity.ScheduleDate;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-08
 */
@RestController
@Api(tags = "日程相关接口")
@RequestMapping("/schedule")
public class ScheduleDateController {

    @Resource
    private ScheduleDateService scheduleDateService;

    @PostMapping("/add")
    public ResultVo addSchedule(String data) {
        ScheduleDate scheduleDate = JSON.toJavaObject(JSON.parseObject(data),ScheduleDate.class);
        return scheduleDateService.save(scheduleDate) ? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @GetMapping("/get")
    public ResultVo getSchedule(String data) {
        Map<String, Object> parseObject= JSON.parseObject(data);
        QueryWrapper<ScheduleDate> ScheduleWrapper = new QueryWrapper<>();
        ScheduleWrapper.like("time",parseObject.get("time"));
        ScheduleWrapper.like("userid",parseObject.get("userid"));
        return new ResultVo(ResultCode.SUCCESS,scheduleDateService.list(ScheduleWrapper));
    }

    @DeleteMapping("/del")
    public ResultVo del(String data) {
        return scheduleDateService.removeById(data.replace("\"","")) ? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @PutMapping("/updateState")
    public ResultVo updateScheduleState(String data) {
        ScheduleDate scheduleDate = JSON.toJavaObject(JSON.parseObject(data),ScheduleDate.class);
        if (scheduleDate.getState().equals("false")){
            scheduleDate.setState("true");
        }else { scheduleDate.setState("false"); }
        return scheduleDateService.updateById(scheduleDate) ? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @GetMapping("/ScheduleComplete")
    public ResultVo getScheduleComplete(String data){
        QueryWrapper<ScheduleDate> CompleteWrapper = new QueryWrapper<>();
        QueryWrapper<ScheduleDate> YCompleteWrapper = new QueryWrapper<>();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        CompleteWrapper.eq("userid",data.replace("\"",""));
        CompleteWrapper.apply("DATE_FORMAT(time,'%Y-%m-%d') >= DATE_FORMAT('" + sdf.format(new Date(date.getTime() - (3600 * 1000 * 24 * 7))) + "','%Y-%m-%d')");
        YCompleteWrapper.eq("userid",data.replace("\"",""));
        YCompleteWrapper.eq("state","true");
        YCompleteWrapper.apply("DATE_FORMAT(time,'%Y-%m-%d') >= DATE_FORMAT('" + sdf.format(new Date(date.getTime() - (3600 * 1000 * 24 * 7)))  + "','%Y-%m-%d')");

        Map<String, Object> remus = new HashMap<>();
        remus.put("Complete",scheduleDateService.count(CompleteWrapper));
        remus.put("YComplete",scheduleDateService.count(YCompleteWrapper));
        return new ResultVo(ResultCode.SUCCESS, remus);
    }
}

