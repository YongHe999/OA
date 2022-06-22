package com.seu.activitis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.seu.activitis.mapper.ProcessFormMapper;
import com.seu.activitis.service.ProcessFormService;
import com.seu.util.entity.ProcessForm;
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
 * @since 2021-08-18
 */
@Service
public class ProcessFormServiceImpl extends ServiceImpl<ProcessFormMapper, ProcessForm> implements ProcessFormService {

    @Resource
    private ProcessFormMapper processFormMapper;

    @Override
    public ResultVo pageListForm(JSONObject params, Page<ProcessForm> page) {
        //获取查询条件
        String formname=params.getString("formname");
        //条件构造器
        QueryWrapper<ProcessForm> queryWrapper=new QueryWrapper<>();
        if (StringUtils.isNotBlank(formname)){
            queryWrapper.like("formname",formname);
        }
        processFormMapper.selectPage(page,queryWrapper);
        return new ResultVo(ResultCode.SUCCESS,new PageResultVo<>(page.getTotal(),page.getSize(),page.getPages(),page.getRecords()));

    }
}
