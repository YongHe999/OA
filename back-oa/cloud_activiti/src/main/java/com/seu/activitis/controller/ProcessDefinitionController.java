package com.seu.activitis.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.seu.activitis.service.ProcessDefinitionService;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/definition")
@Slf4j
public class ProcessDefinitionController {

    @Autowired
    private ProcessDefinitionService processDefinitionService;
    @Resource
    private RepositoryService repositoryService;


    @ApiOperation(value = "流程定义列表")
    @PostMapping("/list")
    public ResultVo list(String data) {
        //封装查询参数
        Map<String, Object> processDefinition= JSON.parseObject(data);
        if (processDefinition == null){
            processDefinition = new HashMap<>();
        }
        if (processDefinition.isEmpty()){
            processDefinition.put("name","");
            processDefinition.put("limit",10);
            processDefinition.put("page",1);
        }
        return processDefinitionService.listProcessDefinition(processDefinition);
    }

    /**
     * 上传文件部署流程定义
     */
    @ApiOperation(value = "上传文件部署流程定义")
    @PostMapping("/upload")
    @CrossOrigin
    @ResponseBody
    public ResultVo upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            // 获取上传的文件名
            String fileName = file.getOriginalFilename();
            ZipInputStream zip = new ZipInputStream(file.getInputStream());
            String extensionName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
            if (!"bpmn".equalsIgnoreCase(extensionName)
                    && !"zip".equalsIgnoreCase(extensionName)
                    && !"bar".equalsIgnoreCase(extensionName)) {
                return new ResultVo(-1,"流程定义文件仅支持 bpmn, zip 格式！");
            }
            if (fileName.endsWith(".zip")){
                repositoryService.createDeployment()
                        .addZipInputStream(zip)
                        .deploy();
            } else {
                repositoryService.createDeployment()
                        .addInputStream(fileName,file.getInputStream())
                        .deploy();
            }
            return new ResultVo(ResultCode.SUCCESS);
        }
            return new ResultVo(-1,"不允许上传空文件！");
    }

    @ApiOperation(value = "删除流程定义")
    @DeleteMapping
    public ResultVo remove(String data) {
        if (data == null) {
            return new ResultVo(-1,"id为空");
        }
        return processDefinitionService.deleteProcessDeploymentByIds(data);
    }

    @ApiOperation(value = "停用或启用")
    @PostMapping( "/suspendOrActiveApply")
    public ResultVo suspendOrActiveApply(String data) {
        Map<String, Object> da= JSON.parseObject(data);
        String id = da.get("id").toString();
        String suspendState = da.get("suspendState").toString();
        if (id == null || suspendState == null) {
            return new ResultVo(-1,"id为空");
        }
        return processDefinitionService.suspendOrActiveApply(id, suspendState);
    }

    /**
     * 读取流程资源
     *
     * @param processDefinitionId 流程定义ID
     * @param resourceName        资源名称
     */
    @RequestMapping(value = "/readResource")
    public void readResource(@RequestParam("pdid") String processDefinitionId, @RequestParam("resourceName") String resourceName, HttpServletResponse response)
            throws Exception {
        ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
        org.activiti.engine.repository.ProcessDefinition pd = pdq.processDefinitionId(processDefinitionId).singleResult();
//        String substring = resourceName.substring(resourceName.lastIndexOf("."));
        // 通过接口读取
        InputStream resourceAsStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), resourceName);
        byte[] b = new byte[1024];
        int len = -1;
         // 输出资源内容到相应对象
        while ((len = resourceAsStream.read(b,0,1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
        response.getOutputStream().close();
    }

    /**
     * 转换流程定义为模型
     * @param processDefinitionId
     * @return
     * @throws UnsupportedEncodingException
     * @throws XMLStreamException
     */
    @PostMapping(value = "/convert2Model")
    @ResponseBody
    public ResultVo convertToModel(@Param("processDefinitionId") String processDefinitionId)
            throws UnsupportedEncodingException, XMLStreamException {
        org.activiti.engine.repository.ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                processDefinition.getResourceName());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

        BpmnJsonConverter converter = new BpmnJsonConverter();
        ObjectNode modelNode = converter.convertToJson(bpmnModel);
        Model modelData = repositoryService.newModel();
        modelData.setKey(processDefinition.getKey());
        modelData.setName(processDefinition.getResourceName());
        modelData.setCategory(processDefinition.getDeploymentId());

        ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());

        repositoryService.saveModel(modelData);

        repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

        return new ResultVo(ResultCode.SUCCESS);
    }

}
