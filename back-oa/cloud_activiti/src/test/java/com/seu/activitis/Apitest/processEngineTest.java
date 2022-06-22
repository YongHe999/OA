package com.seu.activitis.Apitest;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

public class processEngineTest {

    @Test
    public void testProcessEngine(){
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
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        // 断言，判断是否创建成功
        assertNotNull(processEngine);
    }
}
