package com.seu.main.service.impl;

import cn.hutool.core.util.IdUtil;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.seu.main.resource.FileResource;
import com.seu.main.service.FdfsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author LANIAKEA
 * @version 1.0
 * @date 2021-05-18
 */
@Service
public class FdfsServiceImpl implements FdfsService {
    private final String UPLOAD_FOLDER_ARTICLE = "workFile";
    @Resource
    private FileResource fileResource;

    /**
     * @param file
     * @param fileName
     * @param userId
     * @return
     * @throws IOException
     * @throws Exception
     */
    @Override
    public String uploadOSS(MultipartFile file, String fileName, String userId) throws IOException, Exception {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = fileResource.getEndpoint();
// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = fileResource.getAccessKeyId();
        String accessKeySecret = fileResource.getAccessKeySecret();
// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 填写网络流地址。
        InputStream inputStream = file.getInputStream();
//        String uuid = IdUtil.simpleUUID();
        String myObjectName = "";
        myObjectName = UPLOAD_FOLDER_ARTICLE + "/" + userId+ "/" +fileName;


// 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        System.out.println("myObjectName------" + myObjectName);
        ossClient.putObject(fileResource.getBucketName(), myObjectName, inputStream);

// 关闭OSSClient。
        ossClient.shutdown();
        return fileResource.getOssHost() + myObjectName;

    }
}
