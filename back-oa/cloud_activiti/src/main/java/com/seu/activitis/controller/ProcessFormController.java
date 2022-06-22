package com.seu.activitis.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seu.activitis.service.ProcessFormService;
import com.seu.util.entity.ProcessForm;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ajie
 * @date 2021/8/1717:37
 * @email: z-ajie@qq.com
 * 流程App，表单相关操作
 */
@Api(value = "流程表单相关操作")
@RestController
@RequestMapping("/processform")
public class ProcessFormController {

    @Resource
    private ProcessFormService processFormService;

    @ApiOperation(value = "表单列表")
    @GetMapping("/getform")
    public ResultVo getForm(String data) {
        if (data.isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        JSONObject params= JSON.parseObject(data);
        return processFormService.pageListForm(params,new Page<ProcessForm>((int) params.get("page"), (int) params.get("limit"),true));
    }

    @ApiOperation(value = "根据id获取表单")
    @GetMapping("/getFormById")
    public ResultVo getFormById(String data) {
        if (data.isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        List<String> ids = new ArrayList<>();
        String id = data.replace("\"", "");
        ids.add(id);
        return new ResultVo(ResultCode.SUCCESS, processFormService.listByIds(ids));
    }

    @ApiOperation(value = "添加表单")
    @PostMapping
    public ResultVo addForm(String data) {
        ProcessForm processForm = JSON.toJavaObject(JSON.parseObject(data), ProcessForm.class);
        if (processForm.getFormname().isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        //条件构造器
        QueryWrapper<ProcessForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("formname",processForm.getFormname());
        int ver = processFormService.list(queryWrapper).size();
        if (ver>0){
            processForm.setVersion((1+ver)+"");
        }else { processForm.setVersion("1"); }
        return processFormService.save(processForm) ?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @ApiOperation(value = "编辑表单")
    @PutMapping
    public ResultVo EditForm(String data) {
        ProcessForm processForm = JSON.toJavaObject(JSON.parseObject(data), ProcessForm.class);
        if (processForm.getForm().isEmpty() || processForm.getFormname().isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        return processFormService.updateById(processForm) ?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @ApiOperation(value = "删除表单")
    @DeleteMapping
    public ResultVo delForm(String data) {
        if (data == null){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        JSONObject jo=JSONObject.parseObject(data);
        List<String> Ids= JSON.parseArray(jo.getString("ids"),String.class);
        return processFormService.removeByIds(Ids) ?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

}
