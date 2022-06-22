package com.seu.company.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seu.company.Service.WeeklyService;
import com.seu.company.mapper.WeeklyMapper;
import com.seu.util.entity.Weekly;
import com.seu.util.entity.vo.PageResultVo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-11
 */
@Service
public class WeeklyServiceImpl extends ServiceImpl<WeeklyMapper, Weekly> implements WeeklyService {

    @Resource
    private WeeklyMapper weeklyMapper;

    @Override
    public ResultVo WeeklyPageList(JSONObject params, Page<Weekly> page) {
        //获取查询条件
        String state=params.getString("state");
        String title=params.getString("title");
        String btime=params.getString("btime");
        String engtime=params.getString("engtime");
        //条件构造器
        QueryWrapper<Weekly> queryWrapper=new QueryWrapper<>();
        if (StringUtils.isNotBlank(state)){
            queryWrapper.eq("state",state);
        }
        if (StringUtils.isNotBlank(title)){
            queryWrapper.like("title",title);
        }
        if (StringUtils.isNotBlank(btime)&&StringUtils.isNotBlank(engtime)){
            queryWrapper.apply("DATE_FORMAT(time,'%Y-%m-%d') >= DATE_FORMAT('" + btime + "','%Y-%m-%d')")
                        .apply("DATE_FORMAT(time,'%Y-%m-%d') <= DATE_FORMAT('" + engtime + "','%Y-%m-%d')");
        }
        weeklyMapper.selectPage(page,queryWrapper);
        return new ResultVo(ResultCode.SUCCESS,new PageResultVo<>(page.getTotal(),page.getSize(),page.getPages(),page.getRecords()));

    }
}
