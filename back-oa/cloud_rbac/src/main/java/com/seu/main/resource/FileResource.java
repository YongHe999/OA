package com.seu.main.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author LANIAKEA
 * @version 1.0
 * @date 2021-05-19
 */
@Component
@PropertySource("classpath:/aliyun-oss.properties")
@ConfigurationProperties(prefix = "file")
public class FileResource {
    private String host;
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;
    private String ossHost;

    public String getHost() {
        return host;
    }

    public FileResource setHost(String host) {
        this.host = host;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public FileResource setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public FileResource setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public FileResource setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public FileResource setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public FileResource setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public String getOssHost() {
        return ossHost;
    }

    public FileResource setOssHost(String ossHost) {
        this.ossHost = ossHost;
        return this;
    }
}
