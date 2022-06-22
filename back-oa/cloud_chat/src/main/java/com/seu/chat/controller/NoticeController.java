package com.seu.chat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.chat.service.CompanyNoticeService;
import com.seu.util.entity.CompanyNotice;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/10/26 14:40
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    private CompanyNoticeService companyNoticeService;

    @GetMapping
    public ResultVo getNotice(String data){
        JSONObject params = JSON.parseObject(data);
        //获取查询条件
        String title=params.getString("title");
        String btime=params.getString("btime");
        String engtime=params.getString("engtime");
        //条件构造器
        QueryWrapper<CompanyNotice> queryWrapper=new QueryWrapper<>();

        if (StringUtils.isNotBlank(title)){
            queryWrapper.like("title",title);
        }
        if (StringUtils.isNotBlank(btime)&&StringUtils.isNotBlank(engtime)){
            queryWrapper.apply("DATE_FORMAT(time,'%Y-%m-%d') >= DATE_FORMAT('" + btime + "','%Y-%m-%d')")
                    .apply("DATE_FORMAT(time,'%Y-%m-%d') <= DATE_FORMAT('" + engtime + "','%Y-%m-%d')");
        }
        return new ResultVo(ResultCode.SUCCESS,companyNoticeService.list(queryWrapper));
    }
    @DeleteMapping
    public ResultVo del(String data){
        return companyNoticeService.removeById(data.replace("\"",""))?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }
}
