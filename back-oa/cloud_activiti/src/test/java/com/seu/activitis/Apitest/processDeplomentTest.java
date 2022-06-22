package com.seu.activitis.Apitest;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.deploy.Deployer;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * @author Ajie
 * @date 2021/8/710:42
 * @email: z-ajie@qq.com
 */
public class processDeplomentTest {
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private IdentityService identityService;
    private TaskService taskService;
    private RuntimeService runtimeService;
    private HistoryService historyService;
//    //仓库服务，用于管理仓库，比如部署或删除流程定义，读取流程资源等。
//    RepositoryService repositoryService = processEngine.getRepositoryService();
//
//    //运行时服务，管理所有正在运行的流程实例，任务
//    // 对流程部署，流程定义和流程实例的存储服务
//    RuntimeService runtimeService = processEngine.getRuntimeService();
//
//    //任务服务，管理任务。
//    TaskService taskService = processEngine.getTaskService();
//
//    //身份服务，管理用户，组以及它们之间的关系。
//    IdentityService identityService = processEngine.getIdentityService();
//
//    //引擎管理服务，比如管理引擎的配置，数据库和作业等核心对象。
//    ManagementService managementService = processEngine.getManagementService();
//
//    //历史服务，管理历史数据。
//    HistoryService historyService = processEngine.getHistoryService();
    @Before
    public void initProcessEngine(){
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        processEngineConfiguration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/rbac_oa?useUnicode=true&zeroDateTimeBehavior=CONVERT_TO_NULL&characterEncoding=utf8&userSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("root");

        /**
         DB_SCHEMA_UPDATE_FALSE 不自动创建表，需要表存在
         DB_SCHEMA_UPDATE_CREATE_DROP  先删除表再重新创建
         DB_SCHEMA_UPDATE_TRUE  如果表不存在，就自动创建
         */
        processEngineConfiguration.setDatabaseSchemaUpdate(processEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
        //设置是否使用activti自带的用户体系
        processEngineConfiguration.setDbIdentityUsed(false);
        processEngine = processEngineConfiguration.buildProcessEngine();
        // 断言，判断是否创建成功
        assertNotNull(processEngine);

        repositoryService = processEngine.getRepositoryService();
        taskService = processEngine.getTaskService();
        identityService = processEngine.getIdentityService();
        runtimeService = processEngine.getRuntimeService();
        historyService = processEngine.getHistoryService();
    }

     /**
     　　* @description: 部署流程leave.bpmn文件
     　　* @param
     　　* @return
     　　* @author Ajie
     　　* @date 2021/8/7 11:21
     　　*/
    @Test
    public void deployTest() {
        Deployment deployment = repositoryService.createDeployment()
                .name("测试请假流程")
                .addClasspathResource("leave.bpmn")     //根据指定文件发布
                .deploy();
        assertNotNull(deployment);
    }

     /**
     　　* @description: 发起流程申请
     　　* @param
     　　* @return
     　　* @author Ajie
     　　* @date 2021/8/7 11:34
     　　*/
    @Test
    public void submitApplyTest() {
        //设置发起人（申请人id=>对应用户表的登录账号字段）
        String applyUseId = "admin";
        //identityService.setAuthenticatedUserId(userName);//直接调用底层实现，设置申请人，但是需要定义流程定义时配置：activiti:initiator属性
        identityService.setAuthenticatedUserId(applyUseId);

        //流程定义部署在activiti后，就可以在系统中通过activiti去管理该流程的执行，执行流程表示流程的一次执行。
        //比如部署系统出差流程后，如果某用户要申请出差这时就需要执行这个流程，如果另外一个用户也要申请出差则也需要执行该流程，每个执行互不影响，每个执行是单独的流程实例。
        //启动流程实例时，指定的businesskey，就会在act_ru_execution #流程实例的执行表中存储businesskey。
        //Businesskey：业务标识，通常为业务表的主键，业务标识和流程实例一一对应。业务标识来源于业务系统。存储业务标识就是根据业务标识来关联查询业务系统的数据。
        //比如：出差流程启动一个流程实例，就可以将出差单的id作为业务标识存储到activiti中，将来查询activiti的流程实例信息就可以获取出差单的id从而关联查询业务系统数据库得到出差单信息。
        // 根据流程定义的key=>leave启动一个实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("leave","Businesskey");

        System.out.println("部署流程-id："+processInstance.getId());
        System.out.println("部署流程-name："+processInstance.getName());
        System.out.println("业务id=="+processInstance.getBusinessKey());
    }

    @Test
    public void queryTaskTodoTest(){
        //查询我的代办业务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("processKey")   //根据流程定义的key查询
                .taskAssignee("username")              //根据我的用户名查询
                .active()                           //活动中的任务
                .listPage(1,10);

        //查询我的未签收任务
        List<Task> list2 = taskService.createTaskQuery()
//                .processDefinitionKey("processKey")
                .taskCandidateUser("username")
                .active()
                .listPage(1, 10);

//        List<Task> allList = new ArrayList<>();
        //合并为我的所有代办的任务
        list.addAll(list2);
        list.forEach(System.out::println);



         /**
         　　* @description: 完成我的任务
         　　* @param 取一条我的任务数据
         　　* @return
         　　* @author Ajie
         　　* @date 2021/8/7 14:13
         　　*/
         Task task = list.get(0);
         //先把流程是哪一条查出来，传入流程key
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("processKey")
                .singleResult();

        //添加审批意见(传入任务id，实例id，意见)
        //审批意见是谁的（当前用户username）
        identityService.setAuthenticatedUserId("username");
        taskService.addComment(task.getId(),processInstance.getId(),"审批意见：【同意】");

        //添加流程变量
        Map<String, Object> variables = new HashMap<>();
        // deptLeaderApproved同意或不同意的值
        variables.put("deptLeaderApproved",true);

        //先签收任务，再完成
        taskService.claim(task.getId(),"username");
        //完成方法
        taskService.complete(task.getId(),variables);
    }



    /**
     　　* @description: 获取我的已办任务
     　　* @param
     　　* @return
     　　* @author Ajie
     　　* @date 2021/8/7 14:40
     　　*/
    @Test
    public void myTaskDoneTest() {
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("username")      //条件：根据当前我的username查询
                .finished()                 //已经完成的任务
                .listPage(1, 10);
        taskInstanceList.forEach(System.out::println);
    }
    
     /**
     　　* @description: 审批的历史备注
     　　* @param 
     　　* @return 
     　　* @author Ajie
     　　* @date 2021/8/7 14:52 
     　　*/
     @Test
    public void getQueryHistoryComment(){

     }
}
