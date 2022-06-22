package com.seu.company.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seu.util.entity.PunchTheClock;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 阿杰
 * @since 2021-09-27
 */
public interface PunchTheClockService extends IService<PunchTheClock> {
    int ClockComplete(Map<String, Object> params);
}
