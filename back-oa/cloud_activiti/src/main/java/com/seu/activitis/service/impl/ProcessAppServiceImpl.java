package com.seu.activitis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seu.activitis.mapper.ProcessAppMapper;
import com.seu.activitis.service.ProcessAppService;
import com.seu.util.entity.ProcessApp;
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
 * @since 2021-08-25
 */
@Service
public class ProcessAppServiceImpl extends ServiceImpl<ProcessAppMapper, ProcessApp> implements ProcessAppService {

    @Resource
    private ProcessAppMapper processAppMapper;

    @Override
    public ResultVo pageListApp(JSONObject params, Page<ProcessApp> page) {
        //获取查询条件
        String name=params.getString("name");
        //条件构造器
        QueryWrapper<ProcessApp> queryWrapper=new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)){
            queryWrapper.like("name",name);
        }
        processAppMapper.selectPage(page,queryWrapper);
        return new ResultVo(ResultCode.SUCCESS,new PageResultVo<>(page.getTotal(),page.getSize(),page.getPages(),page.getRecords()));

    }
}
