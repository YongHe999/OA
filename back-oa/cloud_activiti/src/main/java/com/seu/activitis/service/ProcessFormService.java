package com.seu.activitis.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seu.util.entity.ProcessForm;
import com.seu.util.entity.vo.ResultVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 阿杰
 * @since 2021-08-18
 */
public interface ProcessFormService extends IService<ProcessForm> {
    ResultVo pageListForm(JSONObject params, Page<ProcessForm> page);
}
