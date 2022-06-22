package com.seu.main.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seu.main.mapper.LogMapper;
import com.seu.main.service.LogService;
import com.seu.main.utils.UserUtil;
import com.seu.util.entity.SysLogs;
import com.seu.util.entity.vo.PageResultVo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/2/28 15:41
 * @description 日志service
 */
@Service
public class LogServiceImpl implements LogService {
    @Resource
    private LogMapper logMapper;

    @Override
    public ResultVo pageListLogs(JSONObject params, Page<SysLogs> page) {
        String nickName=params.getString("nickName");
        String module=params.getString("module");
        String flag=params.getString("flag");

        //条件构造器
        QueryWrapper<SysLogs> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderBy(true,false,"createTime");
        if (StringUtils.isNotBlank(nickName)){
            queryWrapper.like("nickName",nickName);
        }
        if (StringUtils.isNotBlank(module)){
            queryWrapper.like("module",module);
        }
        if (StringUtils.isNotBlank(flag)){
            queryWrapper.eq("flag",flag);
        }

        logMapper.selectPage(page,queryWrapper);
        return new ResultVo(ResultCode.SUCCESS,new PageResultVo<>(page.getTotal(),page.getSize(),page.getPages(),page.getRecords()));
    }

    @Override
    public void save(String module, int flag, String remark) {
        SysLogs sysLogs=new SysLogs();
        String nickName=UserUtil.getLoginUser().getNickname();
        //当前用户名称
        sysLogs.setNickName(StringUtils.isNotBlank(nickName)?nickName:"无");
        //模块
        sysLogs.setModule(module);
        //标志 1成功 0异常
        sysLogs.setFlag(flag);
        //备注，内容
        sysLogs.setRemark(remark);
        //发送时间
        sysLogs.setCreateTime(DateUtil.date());
        sysLogs.setUpdateTime(DateUtil.date());
        logMapper.insert(sysLogs);
    }

    @Override
    public ResultVo delLogsByIds(List<String> ids) {
        return logMapper.deleteBatchIds(ids)>0?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }
}
