package com.seu.company.Controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.company.Service.PunchTheClockService;
import com.seu.company.Service.ScheduleDateService;
import com.seu.util.entity.PunchTheClock;
import com.seu.util.entity.ScheduleDate;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import com.seu.util.utils.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TODO
 *
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/10/28 14:50
 */
@RestController
public class ChartsController {
    @Resource
    private ScheduleDateService scheduleDateService;
    @Resource
    private PunchTheClockService punchTheClockService;

    @GetMapping("/getScheduleCharts")
    public ResultVo getScheduleCharts(String data){

        ArrayList<Object> rows = new ArrayList<>();
        String [] arr= DateUtil.getBeforeSixDay();

        for (String s : arr) {
            JSONObject json = new JSONObject();
            QueryWrapper<ScheduleDate> trueWrapper = new QueryWrapper<>();
            trueWrapper.eq("userid", data.replace("\"", ""));
            trueWrapper.like("time", s);
            trueWrapper.eq("state", "true");
            int trueSchedule = scheduleDateService.count(trueWrapper);

            QueryWrapper<ScheduleDate> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userid", data.replace("\"", ""));
            queryWrapper.like("time", s);
            queryWrapper.eq("state", "false");
            int falseSchedule = scheduleDateService.count(queryWrapper);
            json.put("日期",s);
            json.put("完成事项",trueSchedule);
            json.put("未完成事项",falseSchedule);
            rows.add(json);
        }
        return new ResultVo(ResultCode.SUCCESS,rows);
    }

    @GetMapping("/PunchTheClockCharts")
    public ResultVo getPunchTheClockCharts(String data) {
        ArrayList<Object> rows = new ArrayList<>();
        Map<String,String> dateMap = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        // 前两个月第一天
        Calendar lastTwoC1 = Calendar.getInstance();
        lastTwoC1.add(Calendar.MONTH,-2);
        lastTwoC1.set(Calendar.DAY_OF_MONTH,1);
        dateMap.put("lastTwoC1",format.format(lastTwoC1.getTime()));
        // 前一个月第一天
        Calendar lastOneC1 = Calendar.getInstance();
        lastOneC1.add(Calendar.MONTH,-1);
        lastOneC1.set(Calendar.DAY_OF_MONTH,1);
        dateMap.put("lastOneC1",format.format(lastOneC1.getTime()));
        // 当月第一天
        Calendar cuurC1 = Calendar.getInstance();
        cuurC1.add(Calendar.MONTH,0);
        cuurC1.set(Calendar.DAY_OF_MONTH,1);
        dateMap.put("cuurC1",format.format(cuurC1.getTime()));
        // 当月最后一天
        Calendar cuurC2 = Calendar.getInstance();
        cuurC2.set(Calendar.DAY_OF_MONTH,cuurC2.getActualMaximum(Calendar.DAY_OF_MONTH));
        dateMap.put("cuurC2",format.format(cuurC2.getTime()) + " 23:59:59");
        // 前2月开始的打卡信息
        JSONObject json = new JSONObject();
        QueryWrapper<PunchTheClock> TwoWrapper = new QueryWrapper<>();
        TwoWrapper.eq("userid",data.replace("\"",""));
        TwoWrapper.apply("DATE_FORMAT(ontime,'%Y-%m-%d') >= DATE_FORMAT('" + dateMap.get("lastTwoC1") + "','%Y-%m-%d')")
                .apply("DATE_FORMAT(ontime,'%Y-%m-%d') < DATE_FORMAT('" + dateMap.get("lastOneC1") + "','%Y-%m-%d')");
        List<PunchTheClock> two = punchTheClockService.list(TwoWrapper);
        int j = 0,z = 0;
        for (PunchTheClock theClock : two){
            try {
                Date date2 = format1.parse("09:00:00");
                Date date4 = format1.parse("18:00:00");
                if (null == theClock.getOntime()) {
                    j++;
                }else {
                    Date date = format1.parse(format1.format(theClock.getOntime())); // 先格式化为09:00:00格式，再转Date对象
                    if (date.getTime()>date2.getTime()){
                        j++;
                    }
                }
                if (null == theClock.getOfftime()) {
                    z++;
                }else {
                    Date date3 = format1.parse(format1.format(theClock.getOfftime()));
                    if (date3.getTime()<date4.getTime())
                        z++;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        json.put("日期",dateMap.get("lastTwoC1").substring(0,7));
        json.put("缺勤",lastTwoC1.getActualMaximum(Calendar.DAY_OF_MONTH) - two.size());
        json.put("迟到",j);
        json.put("早退",z);
        rows.add(json);
        // 前1月开始的打卡信息
        JSONObject json2 = new JSONObject();
        QueryWrapper<PunchTheClock> OneWrapper = new QueryWrapper<>();
        OneWrapper.eq("userid",data.replace("\"",""));
        OneWrapper.apply("DATE_FORMAT(ontime,'%Y-%m-%d') >= DATE_FORMAT('" + dateMap.get("lastOneC1") + "','%Y-%m-%d')")
                .apply("DATE_FORMAT(ontime,'%Y-%m-%d') < DATE_FORMAT('" + dateMap.get("cuurC1") + "','%Y-%m-%d')");
        List<PunchTheClock> one = punchTheClockService.list(OneWrapper);
        int j1 = 0,z1 = 0;
        for (PunchTheClock theClock : one){
            try {
                Date date2 = format1.parse("09:00:00");
                Date date4 = format1.parse("18:00:00");
                if (null == theClock.getOntime()) {
                    j1++;
                }else {
                    Date date = format1.parse(format1.format(theClock.getOntime())); // 先格式化为09:00:00格式，再转Date对象
                    if (date.getTime()>date2.getTime()){
                        j1++;
                    }
                }
                if (null == theClock.getOfftime()) {
                    z1++;
                }else {
                    Date date3 = format1.parse(format1.format(theClock.getOfftime()));
                    if (date3.getTime()<date4.getTime())
                        z1++;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        json2.put("日期",dateMap.get("lastOneC1").substring(0,7));
        json2.put("缺勤",lastOneC1.getActualMaximum(Calendar.DAY_OF_MONTH) - one.size());
        json2.put("迟到",j1);
        json2.put("早退",z1);
        rows.add(json2);
        // 这个月开始的打卡信息
        JSONObject json3 = new JSONObject();
        QueryWrapper<PunchTheClock> thisWrapper = new QueryWrapper<>();
        thisWrapper.eq("userid",data.replace("\"",""));
        thisWrapper.apply("DATE_FORMAT(ontime,'%Y-%m-%d') >= DATE_FORMAT('" + dateMap.get("cuurC1") + "','%Y-%m-%d')")
                   .apply("DATE_FORMAT(ontime,'%Y-%m-%d') <= DATE_FORMAT('" + dateMap.get("cuurC2") + "','%Y-%m-%d')");
        List<PunchTheClock> theClockList = punchTheClockService.list(thisWrapper);
        int j2 = 0,z2 = 0;
        for (PunchTheClock theClock : theClockList){
            try {

                Date date2 = format1.parse("09:00:00");
                Date date4 = format1.parse("18:00:00");

                if (null == theClock.getOntime()) {
                    j2++;
                }else {
                    Date date = format1.parse(format1.format(theClock.getOntime())); // 先格式化为09:00:00格式，再转Date对象
                    if (date.getTime()>date2.getTime()){
                        j2++;
                    }
                }
                if (null == theClock.getOfftime()) {
                    z2++;
                }else {
                    Date date3 = format1.parse(format1.format(theClock.getOfftime()));
                    if (date3.getTime()<date4.getTime())
                        z2++;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        json3.put("日期",dateMap.get("cuurC1").substring(0,7));
        json3.put("缺勤",cuurC1.getActualMaximum(Calendar.DAY_OF_MONTH) - theClockList.size());
        json3.put("迟到",j2);
        json3.put("早退",z2);
        rows.add(json3);

        return new ResultVo(ResultCode.SUCCESS,rows);
    }
}
