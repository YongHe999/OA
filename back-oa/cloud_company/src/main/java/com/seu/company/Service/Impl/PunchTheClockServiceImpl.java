package com.seu.company.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seu.company.Service.PunchTheClockService;
import com.seu.company.mapper.PunchTheClockMapper;
import com.seu.util.entity.PunchTheClock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 阿杰
 * @since 2021-09-27
 */
@Service
public class PunchTheClockServiceImpl extends ServiceImpl<PunchTheClockMapper, PunchTheClock> implements PunchTheClockService {

    @Resource
    private PunchTheClockMapper punchTheClockMapper;

    @Override
    public int ClockComplete(Map<String, Object> params) {
        return punchTheClockMapper.ClockComplete(params);
    }
}
