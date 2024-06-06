package com.ercanbeyen.bankingapplication.constant.message;

import com.ercanbeyen.bankingapplication.util.PhotoUtils;

public class ResponseMessages {
    public static final String NOT_FOUND = "%s is not found";
    public static final String ALREADY_EXISTS = "%s already exists";
    public static final String FILE_UPLOAD_SUCCESS = "File is successfully uploaded";
    public static final String FILE_DELETE_SUCCESS = "File is successfully deleted";
    public static final String FILE_UPLOAD_ERROR = "Error occurred while uploading file";
    public static final String DELETE_SUCCESS = "Entity successfully deleted";
    public static final String INVALID_CONTENT_TYPE = "Invalid content type";
    public static final String INVALID_PHOTO_CONTENT_TYPE = INVALID_CONTENT_TYPE + ". Valid content types for photo are " + PhotoUtils.getPlainContentTypes();
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number";

    private ResponseMessages() {}
}
