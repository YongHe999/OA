package com.seu.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seu.util.entity.SysLogs;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/2/28 15:34
 * @description 日志dao
 */
@Mapper
public interface LogMapper extends BaseMapper<SysLogs> {
}
