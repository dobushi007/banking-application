package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    void storeFile(MultipartFile file) throws IOException;
    File getFile(String id);
}
