package com.seu.activitis.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seu.util.entity.ProcessApp;
import com.seu.util.entity.vo.ResultVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 阿杰
 * @since 2021-08-25
 */
public interface ProcessAppService extends IService<ProcessApp> {
    ResultVo pageListApp(JSONObject params, Page<ProcessApp> page);
}
