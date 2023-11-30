package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileUtils {
    private static final int FILE_NAME_LENGTH_THRESHOLD = 100;

    public static void checkIsFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResourceExpectationFailedException("File should not be empty");
        }
    }

    public static void checkLengthOfFileName(MultipartFile file) {
        String fileName = file.getName();
        log.info("File name and its length: {} - {}", fileName, fileName.length());

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null) {
            throw new ResourceExpectationFailedException("File name should not be empty!");
        }

        log.info("File original name and its length: {} - {}", originalFileName, originalFileName.length());

        String[] originalFileNameSplit = originalFileName.split("\\.");
        log.info("originalFileNameSplit array: {}", (Object) originalFileNameSplit);

        if (originalFileNameSplit.length > 2) {
            throw new ResourceExpectationFailedException("Plain file name should not include dot character");
        }

        String plainFileName = originalFileNameSplit[0];
        log.info("Plain file name: {}", plainFileName);

        if (plainFileName.length() > FILE_NAME_LENGTH_THRESHOLD) {
            throw new ResourceExpectationFailedException("File name length threshold is exceeded. Maximum length is " + FILE_NAME_LENGTH_THRESHOLD);
        }
    }
}
