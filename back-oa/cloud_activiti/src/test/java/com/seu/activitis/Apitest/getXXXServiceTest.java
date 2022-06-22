package com.seu.activitis.Apitest;

import org.activiti.engine.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


  /**
  　　* @description: TODO
  　　* @param 
  　　* @return 
  　　* @author Ajie
  　　* @date 2021/8/6 22:03 
  　　*/
public class getXXXServiceTest {

    private ProcessEngine processEngine;

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
    }

     /**
     　　* @description: TODO
     　　* @param 
     　　* @return 
     　　* @author Ajie
     　　* @date 2021/8/6 22:08
     　　*/
    @Test
    public void testGetXXXService(){

        //仓库服务，用于管理仓库，比如部署或删除流程定义，读取流程资源等。
        RepositoryService repositoryService = processEngine.getRepositoryService();
        assertNotNull(repositoryService);

        //运行时服务，管理所有正在运行的流程实例，任务
        // 对流程部署，流程定义和流程实例的存储服务
        RuntimeService runtimeService = processEngine.getRuntimeService();
        assertNotNull(runtimeService);

        //任务服务，管理任务。
        TaskService taskService = processEngine.getTaskService();
        assertNotNull(taskService);

        //身份服务，管理用户，组以及它们之间的关系。
        IdentityService identityService = processEngine.getIdentityService();
        assertNotNull(identityService);

        //引擎管理服务，比如管理引擎的配置，数据库和作业等核心对象。
        ManagementService managementService = processEngine.getManagementService();
        assertNotNull(managementService);

        //历史服务，管理历史数据。
        HistoryService historyService = processEngine.getHistoryService();
        assertNotNull(historyService);


    }
}
