package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface FileStorageService {
    CompletableFuture<File> storeFile(MultipartFile file);
    File getFile(String id);
    String deleteFile(String id);
    Stream<File> getAllFiles();
}
