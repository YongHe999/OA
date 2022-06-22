package com.seu.main.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FdfsService {
    String uploadOSS(MultipartFile file, String fileName, String userId) throws IOException, Exception;
}
