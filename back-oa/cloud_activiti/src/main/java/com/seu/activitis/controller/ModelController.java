package com.seu.activitis.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.seu.activitis.modeler.ModelEditorJsonRestResource;
import com.seu.util.entity.vo.PageResultVo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_DESCRIPTION;
import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_NAME;

/**
 * 流程模型Model操作相关
 * Created by chenhai on 2017/5/23.
 */
@Api(value = "流程模型Model操作相关", tags = {"modeler"})
@RestController
@RequestMapping("/models")
@Slf4j
public class ModelController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ModelEditorJsonRestResource.class);
    @Resource
    private ProcessEngine processEngine;
    @Autowired
    private ObjectMapper objectMapper;
    @Resource
    private RepositoryService repositoryService;


    /**
     * 创建模型
     * @RequestParam("name") String name, @RequestParam("key") String key,
     *                            @RequestParam(value = "description", required = false) String description
     */
    @RequestMapping(value = "/create")
    @ResponseBody
    public ResultVo create(String data) {
        //封装查询参数
        Map<String, Object> model= JSON.parseObject(data);
        if (model.isEmpty()){
            return new ResultVo(-1,"数据为空");
        }
        String name = (String) model.get("name");
        String description = model.get("description").toString();
        String key = model.get("key").toString();
        try {
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);

            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(MODEL_NAME, name);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            description = StringUtils.defaultString(description);
            modelObjectNode.put(MODEL_DESCRIPTION, description);

            Model newModel = repositoryService.newModel();
            newModel.setMetaInfo(modelObjectNode.toString());
            newModel.setName(name);
            newModel.setKey(StringUtils.defaultString(key));

            repositoryService.saveModel(newModel);
            repositoryService.addModelEditorSource(newModel.getId(), editorNode.toString().getBytes("utf-8"));

            return new ResultVo(ResultCode.SUCCESS,newModel.getId());
//            return new AjaxResult(AjaxResult.Type.SUCCESS, "创建模型成功", newModel.getId());
        } catch (Exception e) {
            log.error("创建模型失败:",e.getMessage());
        }
        return new ResultVo(ResultCode.FAIL);
    }

    /**
     * 获取所有模型
     *
     * @return
     */
    @ApiOperation(value = "获取模型,参数为筛选条件，包括分页条件")
    @GetMapping
    public ResultVo modelList(String data) {
        //封装查询参数
        Map<String, Object> modelEntity= JSON.parseObject(data);
        if (modelEntity.isEmpty()){
            modelEntity.put("key","");
            modelEntity.put("name","");
            modelEntity.put("limit",10);
            modelEntity.put("page",1);
        }
        repositoryService = processEngine.getRepositoryService();
        // 创建链式查询
        ModelQuery modelQuery = repositoryService.createModelQuery().orderByLastUpdateTime().desc();

        // 条件过滤
        if (StringUtils.isNotBlank(modelEntity.get("key").toString())) {
            modelQuery.modelKey(modelEntity.get("key").toString());
        }
        if (StringUtils.isNotBlank(modelEntity.get("name").toString())) {
            modelQuery.modelNameLike("%" + modelEntity.get("name") + "%");
        }
        //分页查询
        long size = (int)modelEntity.get("limit");
        long pages = (int)modelEntity.get("page");
        List<Model> resultList = modelQuery.listPage(((int)modelEntity.get("page")-1) * (int)modelEntity.get("limit"), (int)modelEntity.get("limit"));
        //反回分页数据
        return new ResultVo(ResultCode.SUCCESS,new PageResultVo<Model>(modelQuery.count(),size,pages,resultList));
    }

    /**
     * 删除模型
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "删除模型")
    @DeleteMapping
    public Object deleteModel(String data) {
        JSONObject jo=JSONObject.parseObject(data);
        List<String> modelIds= JSON.parseArray(jo.getString("ids"),String.class);
        try {
            for (String id:modelIds){
                repositoryService.deleteModel(id);
            }
            return new ResultVo(ResultCode.SUCCESS);
        }catch (Exception e){
            return new ResultVo(ResultCode.FAIL);
        }
    }

    /**
     * 发布模型为流程定义
     *
     * @param data
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "部署模型为流程定义")
    @PostMapping("/deploymodel")
    public ResultVo deploy(String data) throws Exception {
        JSONObject jo=JSONObject.parseObject(data);
        List<String> modelIds = JSON.parseArray(jo.getString("ids"),String.class);
        //获取模型
        repositoryService = processEngine.getRepositoryService();
        Model modelData = repositoryService.getModel(modelIds.get(0));

        ObjectNode objectNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(objectNode);

        if (bpmnModel.getProcesses().size() == 0) {
            return new ResultVo(-1,"数据模型不符要求，请至少设计一条主线流程。");
        }
        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addBpmnModel(processName,bpmnModel)
                .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        return new ResultVo(ResultCode.SUCCESS,deployment.getId());
    }

    @ApiOperation(value = "上传一个已有模型")
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public void deployUploadedFile(
            @RequestParam("uploadfile") MultipartFile uploadfile) {
        InputStreamReader in = null;
        try {
            try {
                boolean validFile = false;
                String fileName = uploadfile.getOriginalFilename();
                if (fileName.endsWith(".bpmn20.xml") || fileName.endsWith(".bpmn")) {
                    validFile = true;

                    XMLInputFactory xif = XmlUtil.newXMLInputFactory(true);
                    in = new InputStreamReader(new ByteArrayInputStream(uploadfile.getBytes()), "UTF-8");
                    XMLStreamReader xtr = xif.createXMLStreamReader(in);
                    BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

                    if (bpmnModel.getMainProcess() == null || bpmnModel.getMainProcess().getId() == null) {
//                        notificationManager.showErrorNotification(Messages.MODEL_IMPORT_FAILED,
//                                i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_BPMN_EXPLANATION));
                        System.out.println("err1");
                    } else {

                        if (bpmnModel.getLocationMap().isEmpty()) {
//                            notificationManager.showErrorNotification(Messages.MODEL_IMPORT_INVALID_BPMNDI,
//                                    i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_BPMNDI_EXPLANATION));
                            System.out.println("err2");
                        } else {

                            String processName = null;
                            if (StringUtils.isNotEmpty(bpmnModel.getMainProcess().getName())) {
                                processName = bpmnModel.getMainProcess().getName();
                            } else {
                                processName = bpmnModel.getMainProcess().getId();
                            }
                            Model modelData;
                            modelData = repositoryService.newModel();
                            ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
                            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processName);
                            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
                            modelData.setMetaInfo(modelObjectNode.toString());
                            modelData.setName(processName);

                            repositoryService.saveModel(modelData);

                            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
                            ObjectNode editorNode = jsonConverter.convertToJson(bpmnModel);

                            repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
                        }
                    }
                } else {
//                    notificationManager.showErrorNotification(Messages.MODEL_IMPORT_INVALID_FILE,
//                            i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_FILE_EXPLANATION));
                    System.out.println("err3");
                }
            } catch (Exception e) {
                String errorMsg = e.getMessage().replace(System.getProperty("line.separator"), "<br/>");
//                notificationManager.showErrorNotification(Messages.MODEL_IMPORT_FAILED, errorMsg);
                System.out.println("err4");
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
//                    notificationManager.showErrorNotification("Server-side error", e.getMessage());
                    System.out.println("err5");
                }
            }
        }
    }
    /**
     * 导出model的xml文件
     */
    @RequestMapping(value = "/modeler/export/{modelId}")
    public void export(@PathVariable("modelId") String modelId, HttpServletResponse response) {
        try {
            Model modelData = repositoryService.getModel(modelId);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);

            // 流程非空判断
            if (!CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
                byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

                ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
                String filename = bpmnModel.getMainProcess().getId() + ".bpmn";
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                IOUtils.copy(in, response.getOutputStream());
                response.flushBuffer();
            } else {
                try {
                    response.sendRedirect("/modelList");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            LOGGER.error("导出model的xml文件失败：modelId={}", modelId, e);
            try {
                response.sendRedirect("/modelList");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
