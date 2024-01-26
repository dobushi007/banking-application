package com.ercanbeyen.bankingapplication.constant.message;

public class LogMessages {
    public static final String ECHO = "Program is in class {} and method {}";
    public static final String RESOURCE_FOUND = "{} is found";
    public static final String RESOURCE_NOT_FOUND = "{} is not found";
    public static final String RESOURCE_UNIQUE = "{} is unique";
    public static final String EXCEPTION = "Exception message: {}";
    public static final String TRANSACTION_MESSAGE = "Transaction message: {}";
    public static final String BALANCE_UPDATE = "Previous Balance: {} and Next Balance: {}";
    public static final String SCHEDULED_TASK_STARTED = "Scheduled task is started for {}";
    public static final String SCHEDULED_TASK_ENDED = "Scheduled task is ended for {}";
    public static final String BEFORE_REQUEST = "Before sending the request";
    public static final String AFTER_REQUEST = "After sent the request";
    public static final String REST_TEMPLATE_SUCCESS = """
            Response is returned successfully after rest template call.
            Response: {}
            """;
    public static final String RESOURCE_CREATE_SUCCESS = "{} {} is successfully created";
    public static final String CLASS_OF_RESPONSE = "Class of response: {}";
    public static final String CLASS_OF_OBJECT = "Class of {}: {}";
}
