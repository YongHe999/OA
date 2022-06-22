package com.seu.activitis.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seu.activitis.service.ProcessAppService;
import com.seu.util.entity.ProcessApp;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 阿杰
 * @since 2021-08-25
 */
@Api(value = "流程app")
@RestController
@RequestMapping("/processapp")
public class ProcessAppController {
    @Resource
    private ProcessAppService processAppService;

    @ApiOperation(value = "app列表")
    @GetMapping("/getapp")
    public ResultVo getapp(String data) {
        JSONObject params= JSON.parseObject(data);
        if (!params.containsKey("limit")){
            params.put("name","");
            params.put("limit",10);
            params.put("page",1);
        }
        if (data.isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        return processAppService.pageListApp(params,new Page<ProcessApp>((int) params.get("page"), (int) params.get("limit"),true));
    }

    @ApiOperation(value = "添加app")
    @PostMapping
    public ResultVo addapp(String data) {
        ProcessApp app = JSON.toJavaObject(JSON.parseObject(data), ProcessApp.class);
        if (app.getName().isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        //条件构造器
        QueryWrapper<ProcessApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",app.getName());
        int ver = processAppService.list(queryWrapper).size();
        if (ver>0){
            app.setVersion((1+ver)+"");
        }else { app.setVersion("1"); }
        return processAppService.save(app) ?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @ApiOperation(value = "编辑app")
    @PutMapping
    public ResultVo Editapp(String data) {
        ProcessApp processapp = JSON.toJavaObject(JSON.parseObject(data), ProcessApp.class);
        if (processapp.getName().isEmpty() || processapp.getFormId().isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        return processAppService.updateById(processapp) ?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }

    @ApiOperation(value = "删除app")
    @DeleteMapping
    public ResultVo delapp(String data) {
        if (data == null){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        JSONObject jo=JSONObject.parseObject(data);
        List<String> Ids= JSON.parseArray(jo.getString("ids"),String.class);
        return processAppService.removeByIds(Ids) ?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }
}

