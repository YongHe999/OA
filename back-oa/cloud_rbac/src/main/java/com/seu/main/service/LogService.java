package com.seu.main.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.seu.util.entity.SysLogs;
import com.seu.util.entity.vo.ResultVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/2/28 15:39
 * @description 日志service
 */
@Service
public interface LogService {
    /**
     * 保存日志
     * @param module
     * @param flag
     * @param remark
     */
    void save(String module, int flag, String remark);

    /**
     * 分页列表
     * @param params
     * @param page
     * @return
     */
    ResultVo pageListLogs(JSONObject params, Page<SysLogs> page);

    /**
     * 批量删除日志
     * @param ids
     * @return
     */
    ResultVo delLogsByIds(List<String> ids);
}
