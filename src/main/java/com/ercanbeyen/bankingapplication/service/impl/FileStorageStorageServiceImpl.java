package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.repository.FileRepository;
import com.ercanbeyen.bankingapplication.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageStorageServiceImpl implements FileStorageService {
    private final FileRepository fileRepository;

    @Override
    public void storeFile(MultipartFile multipartFile) throws IOException {
        log.info(LogMessages.ECHO_MESSAGE, "FileStorageService", "storeFile");
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        File file = new File(fileName, multipartFile.getContentType(), multipartFile.getBytes());
        fileRepository.save(file);
        log.info("file.getName(): {}", file.getName());
        log.info("File is successfully stored");
    }

    @Override
    public File getFile(String id) {
        log.info(LogMessages.ECHO_MESSAGE, "FileStorageService", "getFile");
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));
    }

    @Override
    public Stream<File> getAllFiles() {
        log.info(LogMessages.ECHO_MESSAGE, "FileStorageService", "getAllFiles");
        return fileRepository.findAll()
                .stream();
    }
}
