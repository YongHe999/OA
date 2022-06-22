package com.seu.activitis.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.util.entity.ProcessApp;
import com.seu.util.entity.vo.PageResultVo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class ProcessDefinitionService {

    @Resource
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Resource
    private ProcessEngine processEngine;

    @Resource
    private ProcessAppService processAppService;

    /**
     * 分页查询流程定义文件
     * @return
     */
    public ResultVo listProcessDefinition(Map<String, Object> Definition) {
        // 流程定义查询对象,用于查询表act_re_procdef，添加条件查询
        ProcessDefinitionQuery query = processEngine.getRepositoryService()
                .createProcessDefinitionQuery();
        // 添加过滤条件
        query.processDefinitionNameLike("%" + Definition.get("name") + "%")
                // 添加排序条件降序排序
                .orderByProcessDefinitionVersion().desc();
        // 添加分页查询
        long size = (int)Definition.get("limit");
        long pages = (int)Definition.get("page");
        List<com.seu.util.entity.ProcessDefinition> Definitionlist = new ArrayList<>();
        List<ProcessDefinition> resultList = query.listPage(((int)Definition.get("page")-1) * (int)Definition.get("limit"), (int)Definition.get("limit"));
        for (ProcessDefinition res: resultList) {
            ProcessDefinitionEntityImpl entityImpl = (ProcessDefinitionEntityImpl) res;
            com.seu.util.entity.ProcessDefinition Definitionentity = new com.seu.util.entity.ProcessDefinition();
            Definitionentity.setId(res.getId());
            Definitionentity.setKey(res.getKey());
            Definitionentity.setName(res.getName());
            Definitionentity.setCategory(res.getCategory());
            Definitionentity.setVersion(res.getVersion());
            Definitionentity.setDescription(res.getDescription());
            Definitionentity.setDeploymentId(res.getDeploymentId());
            Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(res.getDeploymentId())
                    .singleResult();
            Definitionentity.setDeploymentTime(deployment.getDeploymentTime());
            Definitionentity.setDiagramResourceName(res.getDiagramResourceName());
            Definitionentity.setResourceName(res.getResourceName());
            Definitionentity.setSuspendState(entityImpl.getSuspensionState() + "");
            if (entityImpl.getSuspensionState() == 1) {
                Definitionentity.setSuspendStateName("已激活");
            } else {
                Definitionentity.setSuspendStateName("已挂起");
            }
            Definitionlist.add(Definitionentity);
        }
        //反回分页数据
        return new ResultVo(ResultCode.SUCCESS,new PageResultVo<>(query.count(),size,pages,Definitionlist));
    }

    public ResultVo deleteProcessDeploymentByIds(String deploymentIds) {
        JSONObject jo=JSONObject.parseObject(deploymentIds);
        List<String> Ids= JSON.parseArray(jo.getString("ids"),String.class);
        int counter = 0;
        String info = "";
        for (String deploymentId: Ids) {
            List<ProcessInstance> instanceList = runtimeService.createProcessInstanceQuery()
                    .deploymentId(deploymentId)
                    .list();
            if (!CollectionUtils.isEmpty(instanceList)) {
                // 存在流程实例的流程定义
                info="存在运行中的流程实例";
                continue;
            }
            repositoryService.deleteDeployment(deploymentId, true); // 根据部署id删除，true 表示级联删除引用，比如 act_ru_execution 数据
            counter++;
        }
        return new ResultVo(200,"成功：" + counter + "条" + "失败：" + (Ids.size() - counter) + info);
    }

    public ResultVo suspendOrActiveApply(String id, String suspendState) {
        ProcessApp processApp = new ProcessApp();
        QueryWrapper<ProcessApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("definition_id",id);
        if ("1".equals(suspendState)) {
            // 当流程定义被挂起时，已经发起的该流程定义的流程实例不受影响（如果选择级联挂起则流程实例也会被挂起）。
            // 当流程定义被挂起时，无法发起新的该流程定义的流程实例。
            // 直观变化：act_re_procdef 的 SUSPENSION_STATE_ 为 2
            repositoryService.suspendProcessDefinitionById(id);
            processApp.setDefinitionId(id);
            processApp.setSuspendState("2");
            processAppService.update(processApp,queryWrapper);
        } else if ("2".equals(suspendState)) {
            processApp.setDefinitionId(id);
            processApp.setSuspendState("1");
            processAppService.update(processApp,queryWrapper);
            repositoryService.activateProcessDefinitionById(id);
        }
        return new ResultVo(ResultCode.SUCCESS);
    }

}
