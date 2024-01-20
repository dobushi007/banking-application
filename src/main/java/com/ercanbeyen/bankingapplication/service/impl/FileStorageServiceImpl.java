package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.constant.values.ResourceNames;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.repository.FileRepository;
import com.ercanbeyen.bankingapplication.service.FileStorageService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    private final FileRepository fileRepository;

    @Override
    public File storeFile(MultipartFile multipartFile) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        File savedFile;

        try {
            File file = new File(fileName, multipartFile.getContentType(), multipartFile.getBytes());
            savedFile = fileRepository.save(file);
            log.info("File is successfully stored");
        } catch (Exception exception) {
            log.error(LogMessages.EXCEPTION, exception.getMessage());
            String message = ResponseMessages.FILE_UPLOAD_ERROR;
            throw new ResourceExpectationFailedException(message);
        }

        return savedFile;
    }

    @Override
    public File getFile(String id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        return findFileById(id);
    }

    @Override
    public String deleteFile(String id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        fileRepository.findById(id).ifPresentOrElse(file -> {
            log.info(LogMessages.RESOURCE_FOUND, ResourceNames.FILE);

            try {
                fileRepository.delete(file);
            } catch (Exception exception) {
                log.error(LogMessages.EXCEPTION, exception.getMessage());
                String message = "File is a profile photo. So, it might only be deleted from customer api";
                throw new ResourceExpectationFailedException(message);
            }
        }, () -> {
            log.error(LogMessages.RESOURCE_NOT_FOUND, ResourceNames.FILE);
            throw new ResourceNotFoundException(ResponseMessages.NOT_FOUND);
        });

        return ResponseMessages.FILE_DELETE_SUCCESS;
    }

    @Override
    public Stream<File> getAllFiles() {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        return fileRepository.findAll()
                .stream();
    }

    private File findFileById(String id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.FILE)));
    }
}
