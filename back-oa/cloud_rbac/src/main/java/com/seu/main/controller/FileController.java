package com.seu.main.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.main.dto.LoginUser;
import com.seu.main.resource.FileResource;
import com.seu.main.service.FdfsService;
import com.seu.main.service.FileCloudService;
import com.seu.main.service.TokenService;
import com.seu.util.entity.FileCloud;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 阿里云oss上传
 * @author LANIAKEA
 * @version 1.0
 * @date 2021-07-09
 */
@RestController
@RequestMapping("/fs")
public class FileController {

    @Resource
    private FdfsService  fdfsService;
    @Resource
    private FileResource fileResource;
    @Resource
    private FileCloudService fileCloudService;
    @Resource
    private TokenService tokenService;

    @PostMapping("/uploadSomeFiles")
    public ResultVo uploadSomeFiles(String userId,@RequestParam("file") MultipartFile[] files) throws Exception {
        // 声明list，用于存放多个图片的地址路径，返回到前端
        List<FileCloud> filesUrlList = new ArrayList<>();
        if (files != null && files.length > 0) {
            String fileName = "";
            for (MultipartFile file : files) {
                String path = "";
                if (file != null) {
                    // 获得文件上传的名称
                    fileName = file.getOriginalFilename();

                    // 判断文件名不能为空
                    if (StringUtils.isNotBlank(fileName)) {
                        String fileNameArr[] = fileName.split("\\.");
                        // 获得后缀
                        String suffix = fileNameArr[fileNameArr.length - 1];
                        // 判断后缀符合我们的预定义规范
                        if (!suffix.equalsIgnoreCase("png") &&
                                !suffix.equalsIgnoreCase("jpg") &&
                                !suffix.equalsIgnoreCase("xml") &&
                                !suffix.equalsIgnoreCase("doc") &&
                                !suffix.equalsIgnoreCase("docx") &&
                                !suffix.equalsIgnoreCase("txt") &&
                                !suffix.equalsIgnoreCase("xls") &&
                                !suffix.equalsIgnoreCase("xlsx") &&
                                !suffix.equalsIgnoreCase("ppt") &&
                                !suffix.equalsIgnoreCase("pptx") &&
                                !suffix.equalsIgnoreCase("jpeg")
                        ) {
                            continue;
                        }
                        // 执行上传
//                        path = uploaderService.uploadFdfs(file, suffix);
                        path = fdfsService.uploadOSS(file, fileName, userId);

                    } else {
                        continue;
                    }
                } else {
                    continue;
                }

                String finalPath = "";
                if (StringUtils.isNotBlank(path)) {
//                    finalPath = fileResource.getHost() + path;
                    finalPath =  path;
                    FileCloud fileCloud = new FileCloud();
                    fileCloud.setFilename(fileName);
                    fileCloud.setUrl(finalPath);
                    fileCloud.setUserid(userId);
                    fileCloud.setShare("N");
                    fileCloud.setUploadtime(new Date());
                    filesUrlList.add(fileCloud);
                    // 删除同名的文件，只保留最新
                    QueryWrapper<FileCloud> fileCloudQueryWrapper = new QueryWrapper<>();
                    fileCloudQueryWrapper.eq("filename",fileName);
                    fileCloudService.remove(fileCloudQueryWrapper);
                }
            }
            fileCloudService.saveBatch(filesUrlList, filesUrlList.size());
            return new ResultVo(200,"已上传",filesUrlList);
        }

        return new ResultVo(ResultCode.FAIL,filesUrlList);
    }


    /**
     * 查询用户的文件列表
     * @return ResultVo
     */
    @GetMapping("/byUser")
    public ResultVo getFileList(String data,HttpServletRequest request){
        LoginUser loginUser= tokenService.getLoginUser(request.getHeader("token"));
        JSONObject fileData = JSON.parseObject(data);
        QueryWrapper<FileCloud> fileCloudQueryWrapper = new QueryWrapper<>();
        fileCloudQueryWrapper.like("filename",fileData.getString("name"));
        fileCloudQueryWrapper.eq("userId",loginUser.getId());
        return new ResultVo(ResultCode.SUCCESS, fileCloudService.list(fileCloudQueryWrapper));
    }
    @GetMapping("/AllShareFile")
    public ResultVo getAllShareFile(String data){
        JSONObject fdata = JSON.parseObject(data);
        QueryWrapper<FileCloud> fileCloudQueryWrapper = new QueryWrapper<>();
        fileCloudQueryWrapper.like("filename",fdata.getString("name"));
        fileCloudQueryWrapper.like("share","Y");
        return new ResultVo(ResultCode.SUCCESS, fileCloudService.list(fileCloudQueryWrapper));
    }
    @PutMapping("/share")
    public ResultVo ShareFile(String data){
        FileCloud fileCloud = JSON.toJavaObject(JSON.parseObject(data),FileCloud.class);
        if (null!=fileCloud.getShare() && fileCloud.getShare().equals("N")){
            fileCloud.setShare("Y");
        }else {
            fileCloud.setShare("N");
        }
        fileCloudService.updateById(fileCloud);
        return new ResultVo(ResultCode.SUCCESS);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('sys_file_del')")
    public ResultVo del(HttpServletRequest request,String data){
        JSONObject jsonObject = JSON.parseObject(data);
        OSS ossClient = new OSSClientBuilder().build(fileResource.getEndpoint(), fileResource.getAccessKeyId(), fileResource.getAccessKeySecret());
        LoginUser loginUser= tokenService.getLoginUser(request.getHeader("token"));
        if (!jsonObject.getString("uuid").equals(loginUser.getId())){
            return new ResultVo(-1,"不能删除其他用户的文件");
        }
        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(fileResource.getBucketName(), "workFile/" +loginUser.getId()+ "/" + jsonObject.getString("filename"));
        // 关闭OSSClient。
        ossClient.shutdown();
        // 删除同名的文件
        QueryWrapper<FileCloud> fileCloudQueryWrapper = new QueryWrapper<>();
        fileCloudQueryWrapper.eq("filename",jsonObject.getString("filename"));
        fileCloudService.remove(fileCloudQueryWrapper);
        return new ResultVo(ResultCode.SUCCESS);
    }

    /**
     * 预览
     * @param data
     * @return ResultVo
     */
    @GetMapping("/preview")
    public ResultVo preview(String data){
        JSONObject jsonObject = JSON.parseObject(data);
// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(fileResource.getEndpoint(), fileResource.getAccessKeyId(), fileResource.getAccessKeySecret());
// 设置样式，样式中包含文档预览参数。
        String style = "imm/previewdoc,copy_1";

// 指定过期时间为10分钟。
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10 );
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(fileResource.getBucketName(), "workFile/" +jsonObject.getString("uuid")+ "/" + jsonObject.getString("filename"), HttpMethod.GET);
        req.setExpiration(expiration);
        req.setProcess(style);
        URL signedUrl = ossClient.generatePresignedUrl(req);
// 关闭OSSClient。
        ossClient.shutdown();
        return new ResultVo(ResultCode.SUCCESS,signedUrl);
    }

    @GetMapping("/previewImg")
    public ResultVo previewImg(String data){
        JSONObject jsonObject = JSON.parseObject(data);

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(fileResource.getEndpoint(), fileResource.getAccessKeyId(), fileResource.getAccessKeySecret());

// 指定签名URL过期时间为10分钟。
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 20 );
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(fileResource.getBucketName(), "workFile/" +jsonObject.getString("uuid")+ "/" + jsonObject.getString("filename"));
        req.setExpiration(expiration);
        URL signedUrl = ossClient.generatePresignedUrl(req);
        System.out.println(signedUrl);
// 关闭OSSClient。
        ossClient.shutdown();
        return new ResultVo(ResultCode.SUCCESS,signedUrl);
    }
}
