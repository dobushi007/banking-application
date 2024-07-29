package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@UtilityClass
public class PhotoUtils {
    private final List<String> validContentTypes = List.of(
            "image/png", "image/jpg", "image/jpeg"
    );

    public void checkPhoto(MultipartFile file) {
        FileUtils.checkIsFileEmpty(file);
        checkContentTypeOfPhoto(file);
        FileUtils.checkLengthOfFileName(file);
    }

    public List<String> getPlainContentTypes() {
        return FileUtils.getPlainContentTypes(validContentTypes);
    }

    private void checkContentTypeOfPhoto(MultipartFile file) {
        if (!validContentTypes.contains(file.getContentType())) {
            throw new ResourceExpectationFailedException(ResponseMessages.INVALID_PHOTO_CONTENT_TYPE);
        }
    }
}
