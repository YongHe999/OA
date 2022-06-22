package com.seu.company.Controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.company.Service.PunchTheClockService;
import com.seu.company.feign.ProcessStartService;
import com.seu.util.entity.PunchTheClock;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 阿杰
 * @since 2021-09-27
 */
@RestController
@Api(tags = "打卡")
@RequestMapping("/punchClock")
public class PunchTheClockController {
    @Resource
    private PunchTheClockService punchTheClockService;

    @Resource
    private ProcessStartService processStartService;


    @ApiOperation("通过用户得到打卡信息")
    @GetMapping("/getPunchClockByUser")
    public ResultVo getPunchClock(String data){
        String userId = data.replace("\"", "");
        QueryWrapper<PunchTheClock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid",userId);
        return new ResultVo(ResultCode.SUCCESS,punchTheClockService.list(queryWrapper));
    }
    @ApiOperation("本周打卡完成度")
    @GetMapping("/PunchClockComplete")
    public ResultVo PunchClockComplete(String data){
        Map<String, Object> params = new HashMap<>();
        Date date = new Date();
        params.put("userid",data.replace("\"",""));
        params.put("btime",new Date(date.getTime() - (3600 * 1000 * 24 * 7)));
        params.put("etime",new Date(date.getTime() + (3600 * 1000 * 24)));
        return new ResultVo(ResultCode.SUCCESS,punchTheClockService.ClockComplete(params));
    }

    @ApiOperation("得到某一天的打卡")
    @GetMapping("/getClockByUserAndDay")
    public ResultVo getClockByUserAndDay(String data){
        Map<String, Object> jo= JSON.parseObject(data);
        QueryWrapper<PunchTheClock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid",jo.get("userid"));
        queryWrapper.like("ontime",jo.get("ontime"));
        return new ResultVo(ResultCode.SUCCESS,punchTheClockService.list(queryWrapper));
    }

    @ApiOperation("打卡")
    @PostMapping
    public ResultVo addPunchClock(String data){
        Map<String, Object> jo= JSON.parseObject(data);
        double COMPANY_LAT = Double.parseDouble(jo.get("COMPANY_LAT").toString());
        if (null == jo.get("lat")) {
            return new ResultVo(-1,"无定位信息，请重新定位");
        }
        double lat = Double.parseDouble(jo.get("lat").toString());
        double COMPANY_LNG = Double.parseDouble(jo.get("COMPANY_LNG").toString());
        double lng = Double.parseDouble(jo.get("lng").toString());
        double distance = distance(COMPANY_LAT,lat,COMPANY_LNG,lng);
        PunchTheClock punchTheClock = JSON.toJavaObject(JSON.parseObject(data),PunchTheClock.class);

        // 先打早班卡
        QueryWrapper<PunchTheClock> queryWrapper = new QueryWrapper<>();
        String time = jo.get("ontime").toString().substring(0,jo.get("ontime").toString().indexOf(" "));
        queryWrapper.eq("userid",punchTheClock.getUserid());
        queryWrapper.like("ontime",time);
        List<PunchTheClock> punchTheClockList = punchTheClockService.list(queryWrapper); // 查询上班卡时间是否存在
        if (punchTheClockList.size()<1){ // 没有早班卡，去除晚班时间
            punchTheClock.setOfftime(null);
            punchTheClock.setOffpunch(null);
            if (jo.get("isPhone").toString().equals("false")){ // PC端不做距离判断
                return punchTheClockService.save(punchTheClock)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
            }else {
                if (distance<500){ // 移动端定位判定距离
                    return punchTheClockService.save(punchTheClock)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
                }
                return new ResultVo(-1,"距离大于500米，请靠近点再试");
            }
        }else {
            if (null!=punchTheClockList.get(0).getOffpunch()&&punchTheClockList.get(0).getOffpunch().equals("true")){
                return new ResultVo(-1,"今日打卡已完成");
            }

            QueryWrapper<PunchTheClock> PunchWrapper = new QueryWrapper<>();
            PunchWrapper.eq("userid",punchTheClock.getUserid());
            PunchWrapper.like("ontime",time);
            punchTheClock.setOntime(null); // 去除早班时间
            punchTheClock.setOnpunch(null);
            if (jo.get("isPhone").toString().equals("false")){ // PC端不做距离判断
                return punchTheClockService.update(punchTheClock,PunchWrapper)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
            }else {
                if (distance<500){ // 移动端定位判定距离
                    return punchTheClockService.update(punchTheClock,PunchWrapper)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
                }
                return new ResultVo(-1,"距离大于500米，请靠近点再试");
            }
        }
    }

    @ApiOperation("修改打卡")
    @PutMapping
    public ResultVo updatePunchClock(String data){
        PunchTheClock punchTheClock = JSON.toJavaObject(JSON.parseObject(data),PunchTheClock.class);
        return punchTheClockService.updateById(punchTheClock)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @ApiOperation("获得流程")
    @PostMapping("/getApp")
    public ResultVo getApp(String data){
        return processStartService.getApp(data);
    }
    @ApiOperation("补卡申请")
    @PostMapping("/PunchClockApply")
    public ResultVo PunchClockApply(String data){
        return processStartService.startProcess(data);
    }

    /**
     * 求两个经纬度之间的距离
     */
    public static double distance(double COMPANY_LAT, double lat2, double COMPANY_LNG, double lng2) {
        final int r = 6371;
        double latDistance = Math.toRadians(lat2 - COMPANY_LAT);
        double lonDistance = Math.toRadians(lng2 - COMPANY_LNG);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(COMPANY_LAT)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = r * c * 1000;
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }
}

