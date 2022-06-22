package com.seu.activitis.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.activitis.service.ProcessBusinessService;
import com.seu.util.entity.ProcessBusiness;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.*;

/**
 * @author Ajie
 * @date 2021/8/17 14:44
 * @email: z-ajie@qq.com
 */

@RestController
@Api(value = "任务相关")
@RequestMapping("/task")
public class TasksController {
    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private HistoryService historyService;
    @Resource
    private IdentityService identityService;
    @Resource
    private ProcessBusinessService processBusinessService;

    @ApiOperation(value = "用户任务列表")
    @GetMapping
    public ResultVo getTaskbyUser (String data) {
        //封装查询参数
        Map<String, Object> task= JSON.parseObject(data);
        if (task.isEmpty() || !task.containsKey("userId")){
            return new ResultVo(-1,"用户参数为空");
        }if (!task.containsKey("processkey")){
            task.put("processkey","");
        }

        //查询我的代办业务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionNameLike("%" + task.get("processname") + "%")
                .processDefinitionKeyLike("%" + task.get("processkey") + "%")   //根据流程定义的key查询
                .taskAssignee(task.get("userId").toString())              //根据我的用户名查询
                .active()                           //活动中的任务
                .list();

        //查询我的未签收任务
        List<Task> list2 = taskService.createTaskQuery()
                .processDefinitionNameLike("%" + task.get("processname") + "%")
                .processDefinitionKeyLike("%" + task.get("processkey") + "%")   //根据流程定义的key查询
                .taskCandidateUser(task.get("userId").toString())
                .active()
                .list();

        //        List<Task> allList = new ArrayList<>();
        //合并为我的所有代办的任务
        list.addAll(list2);
        list.forEach(System.out::println);

        // ============= 对上面查询结果进行封装 ==================
        List<Map<String, Object>> Result = new ArrayList<>();
        for (Task grog : list) {
            Map<String, Object> pdMap = new HashMap<>();
            pdMap.put("id", grog.getId());
            pdMap.put("DelegationState", grog.getDelegationState());
            pdMap.put("CreateTime", grog.getCreateTime());
            pdMap.put("name", grog.getName());
            pdMap.put("Assignee", grog.getAssignee());
            pdMap.put("Owner", grog.getOwner());
            pdMap.put("Description", grog.getDescription());
            pdMap.put("Priority", grog.getPriority());
            pdMap.put("TaskDefinitionKey", grog.getTaskDefinitionKey());
            pdMap.put("Category", grog.getCategory());
            pdMap.put("ClaimTime", grog.getClaimTime()); // 任务签收时间
            pdMap.put("ProcessInstanceId", grog.getProcessInstanceId());
            pdMap.put("ProcessDefinitionId", grog.getProcessDefinitionId());
            Result.add(pdMap);
        }
        return new ResultVo(ResultCode.SUCCESS,Result);
    }
    /**
     * @Description: TODO(根据角色Id查找所有的代办任务)
     * @param data 角色Id
     */
    @GetMapping("/getTaskByGroupId")
    public List<Map<String, Object>> findMyTaskListByGroupId(String data) {
        String groupName = data.replace("\"", "");
        List<Task> List = taskService.createTaskQuery()
//                .taskCandidateGroupIn(groupId)
                .taskCandidateGroup(groupName)
                .list();
        // ============= 对上面查询结果进行封装 ==================
        List<Map<String, Object>> TaskList = new ArrayList<>();
        for (Task grog : List) {
            Map<String, Object> dMap = new HashMap<>();
            dMap.put("id", grog.getId());
            dMap.put("DelegationState", grog.getDelegationState());
            dMap.put("CreateTime", grog.getCreateTime());
            dMap.put("name", grog.getName());
            dMap.put("Assignee", grog.getAssignee());
            dMap.put("Owner", grog.getOwner());
            dMap.put("Description", grog.getDescription());
            dMap.put("Priority", grog.getPriority());
            dMap.put("TaskDefinitionKey", grog.getTaskDefinitionKey());
            dMap.put("Category", grog.getCategory());
            dMap.put("ProcessInstanceId", grog.getProcessInstanceId());
            TaskList.add(dMap);
        }
        return TaskList;
    }

    @ApiOperation(value = "根据taskId查找businessKey")
    @GetMapping("/getBusinessKey")
    public String getBusinessKeyByTaskId(String data){
        String taskId = data.replace("\"", "");
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        return pi.getBusinessKey()+",";
    }

    @ApiOperation(value = "获取申请业务详情")
    @GetMapping("/getBusiness")
    public List<ProcessBusiness> getBusiness(String data){
        String businessId = data.replace("\"", "");
        QueryWrapper<ProcessBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",businessId);
        return processBusinessService.list(queryWrapper);
    }

    @ApiOperation(value = "签收一条任务")
    @PostMapping("/claimTask")
    public ResultVo claim(String data){
        Map<String, Object> task= JSON.parseObject(data);
        taskService.claim(task.get("taskId").toString(),task.get("userId").toString());
        return new ResultVo(ResultCode.SUCCESS,"签收成功");
    }

    @ApiOperation(value = "转办")
    @PostMapping("/resolveTask")
    public ResultVo resolve(String data){
        Map<String, Object> task= JSON.parseObject(data);
        if (task.get("userId").equals(""))
            return new ResultVo(ResultCode.FAIL,"请选择转办人");
        taskService.setAssignee(task.get("taskId").toString(),task.get("userId").toString());
        return new ResultVo(ResultCode.SUCCESS,"转办成功");
    }


    @PostMapping("/cancel")
    @ApiOperation(value = "撤回申请")
    public ResultVo cancel(String data){
        Map<String, Object> cancelTask= JSON.parseObject(data);
        if(!cancelTask.containsKey("reason")){
            cancelTask.put("reason","");
        }else if (!cancelTask.containsKey("procInstId")) {
            return new ResultVo(ResultCode.FAIL,"无任务实例id");
        }
        runtimeService.deleteProcessInstance(cancelTask.get("procInstId").toString(), "canceled:"+cancelTask.get("reason"));
        historyService.deleteHistoricProcessInstance(cancelTask.get("procInstId").toString());

        if (cancelTask.get("businessId") != null && !cancelTask.get("businessId").equals("") && !cancelTask.get("businessId").equals("null"))
            processBusinessService.removeById(cancelTask.get("businessId").toString());
        return new ResultVo(ResultCode.SUCCESS,"已撤回");
    }

    /**
     　　* @description: 完成我的任务
     　　* @param 一条我的任务
     　　* @return
     　　* @author Ajie
     　　* @date 2021/8/7 14:13
     　　*/
    @ApiOperation(value = "完成一条任务")
    @PostMapping("/doneTask")
    public ResultVo doneMyTask(String data){
        Map<String, Object> taskdata= JSON.parseObject(data);
        if (!taskdata.containsKey("taskId") || !taskdata.containsKey("processId")){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        //先把流程是哪一条查出来，传入流程key
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//                .processDefinitionKey(taskdata.get("processId").toString())
//                .singleResult();

        //添加审批意见(传入任务id，实例id，意见)
        //审批意见是谁的（当前用户username）
        identityService.setAuthenticatedUserId(taskdata.get("username").toString());
        taskService.addComment(taskdata.get("taskId").toString(),taskdata.get("processId").toString(),"审批意见："+taskdata.get("comments"));

        //添加流程变量,箭头走向的值
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> varData= JSON.parseObject(taskdata.get("variables").toString()); // 得到变量数据
        Set<String> mpaSet = varData.keySet(); // 获取所有的key值为set的集合
        // 获取key的iterator遍历
        // 当前key值
        for (String key : mpaSet) { // 存在下一个
            if (StringUtils.substringBefore(key, "y").equals("appl")) {
                System.out.println(key);
                variables.put(key, varData.get(key));
            }
        }

        // 被委派人处理完成任务
        // p.s. 被委托的流程需要先 resolved 这个任务再提交。
        // 所以在 complete 之前需要先 resolved
        // resolveTask() 要在 claim() 之前，不然 act_hi_taskinst 表的 assignee 字段会为 null
        taskService.resolveTask(taskdata.get("taskId").toString(), variables);
        //先签收任务，再完成
        taskService.claim(taskdata.get("taskId").toString(),taskdata.get("username").toString());
        //完成方法
        taskService.complete(taskdata.get("taskId").toString(),variables);
        return new ResultVo(ResultCode.SUCCESS,"流程已向下执行");
    }

    /**
     　　* @description: 发起流程申请
     　　* @param
     　　* @return
     　　* @author Ajie
     　　* @date 2021/8/7 11:34
     　　*/
    @ApiOperation(value = "发起申请，启动流程实例")
    @PostMapping("/startprocess")
    public ResultVo submitApplyTest(String data) {
        Map<String, Object> jo= JSON.parseObject(data);
        ProcessBusiness processBusiness = JSON.toJavaObject(JSON.parseObject(data), ProcessBusiness.class);
        String applyUseId = "";
        //设置发起人（申请人id=>对应用户表的登录账号字段）
        if (!jo.containsKey("user") || !jo.containsKey("definitionid")){
            return new ResultVo(-1,"无申请人或流程定义ID");
        }else {
            applyUseId = jo.get("user").toString();
        }

        //直接调用底层实现，设置申请人，但是需要定义流程定义时配置：activiti:initiator属性
        identityService.setAuthenticatedUserId(applyUseId);

         /*
         * @description: 保存所用表单数据到业务表，返回id
         */
        String businesskey = "";
        if (!processBusiness.getFormdata().isEmpty()) {
            if (processBusinessService.save(processBusiness))
                businesskey =  processBusiness.getId();
        }
        //添加流程变量
        Map<String, Object> variables = new HashMap<>();

        Map<String, Object> varData= JSON.parseObject(jo.get("variables").toString()); // 得到变量数据
        Set<String> mpaSet = varData.keySet(); // 获取所有的key值为set的集合
        // 获取key的iterator遍历
        // 当前key值
        for (String key : mpaSet) { // 存在下一个
            System.out.println(key);
            if (StringUtils.substringBefore(key, "y").equals("appl")) {
                variables.put(key, varData.get(key).toString());
            }
        }
        variables.put("businessKey",businesskey);
        //流程定义部署在activiti后，就可以在系统中通过activiti去管理该流程的执行，执行流程表示流程的一次执行。
        //比如部署系统出差流程后，如果某用户要申请出差这时就需要执行这个流程，如果另外一个用户也要申请出差则也需要执行该流程，每个执行互不影响，每个执行是单独的流程实例。
        //启动流程实例时，指定的businesskey，就会在act_ru_execution #流程实例的执行表中存储businesskey。
        //Businesskey：业务标识（业务表的ID），通常为业务表的主键，业务标识和流程实例一一对应。业务标识来源于业务系统。存储业务标识就是根据业务标识来关联查询业务系统的数据。
        //比如：出差流程启动一个流程实例，就可以将出差单的id作为业务标识存储到activiti中，将来查询activiti的流程实例信息就可以获取出差单的id从而关联查询业务系统数据库得到出差单信息。
        // 根据流程定义的key=>leave启动一个实例
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceById(jo.get("definitionid").toString(),businesskey,variables);
//        runtimeService.setProcessInstanceName(jo.get("definitionid").toString(),jo.get("instanceName").toString());
        System.out.println("流程实例-id："+processInstance.getId());
        System.out.println("流程实例名称："+processInstance.getName());
        System.out.println("流程申请人："+processInstance.getStartUserId());
        System.out.println("流程申请时间："+processInstance.getStartTime());
        System.out.println("流程定义-id："+processInstance.getProcessDefinitionId());
        System.out.println("业务id==："+processInstance.getBusinessKey());
        return new ResultVo(ResultCode.SUCCESS,"申请人："+processInstance.getStartUserId());
    }
}
