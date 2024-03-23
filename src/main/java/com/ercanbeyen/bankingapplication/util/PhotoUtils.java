package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@UtilityClass
public final class PhotoUtils {
    private static final List<String> validContentTypeList = List.of(
            "image/png", "image/jpg", "image/jpeg"
    );

    public static void checkPhoto(MultipartFile file) {
        FileUtils.checkIsFileEmpty(file);
        checkContentTypeOfPhoto(file);
        FileUtils.checkLengthOfFileName(file);
    }

    private static void checkContentTypeOfPhoto(MultipartFile file) {
        if (!validContentTypeList.contains(file.getContentType())) {
            List<String> plainContentTypeList = FileUtils.getPlainContentTypes(validContentTypeList);
            throw new ResourceExpectationFailedException("Invalid content type for photo. Valid content types are " + plainContentTypeList);
        }
    }
}
