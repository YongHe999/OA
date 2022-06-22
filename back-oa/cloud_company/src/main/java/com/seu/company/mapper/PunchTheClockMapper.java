package com.seu.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seu.util.entity.PunchTheClock;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 阿杰
 * @since 2021-09-27
 */
public interface PunchTheClockMapper extends BaseMapper<PunchTheClock> {
    int ClockComplete(@Param("params") Map<String, Object> params);
}
