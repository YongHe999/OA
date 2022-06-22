package com.seu.activitis.Apitest;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ajie
 * @date 2021/8/6 22:07
 * @email: z-ajie@qq.com
 */
public class getXXXQueryTest {
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
     　　* @param 测试链式调用，链式查询始终返回一个对象
     　　* @return 
     　　* @author Ajie
     　　* @date 2021/8/6 22:10 
     　　*/
    @Test
    public void testLinkQueryEqualObject(){

        ProcessDefinitionQuery processDefinitionQuery = processEngine.getRepositoryService().createProcessDefinitionQuery();

        ProcessDefinitionQuery active = processDefinitionQuery.active();
        //使用断言方法判断active()执行查询的结果与processDefinitionQuery是否一致
        assertEquals(processDefinitionQuery,active);
    }
}
