package com.seu.company.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seu.util.entity.Weekly;
import com.seu.util.entity.vo.ResultVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-11
 */
public interface WeeklyService extends IService<Weekly> {
    ResultVo WeeklyPageList(JSONObject params, Page<Weekly> page);
}
