package com.ercanbeyen.bankingapplication.factory;

import com.ercanbeyen.bankingapplication.entity.File;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MockFileFactory {
    public static File generateMockFile() throws IOException {
        MultipartFile multipartFile = generateMultipartFile();
        return new File(multipartFile.getName(), multipartFile.getContentType(), multipartFile.getBytes());
    }

    public static MultipartFile generateMockMultipartFile() {
        return generateMultipartFile();
    }

    private static MultipartFile generateMultipartFile() {
        return new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.IMAGE_PNG_VALUE,
                "Hello, World!".getBytes());
    }
}
