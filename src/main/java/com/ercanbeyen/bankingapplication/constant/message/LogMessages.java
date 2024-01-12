package com.ercanbeyen.bankingapplication.constant.message;

public class LogMessages {
    public static final String ECHO = "Program is in class {} and method {}";
    public static final String RESOURCE_FOUND = "{} is found";
    public static final String RESOURCE_NOT_FOUND = "{} is not found";
    public static final String EXCEPTION = "Exception message: {}";
    public static final String TRANSACTION_MESSAGE = "Transaction message: {}";
    public static final String BALANCE_UPDATE = "Previous Balance: {} and Next Balance: {}";
    public static final String TASK_CREATED = "Task {} is successfully created";

    public static class ResourceNames {
        public static final String CUSTOMER = "Customer";
        public static final String ACCOUNT = "Account";
        public static final String ADDRESS = "Address";
        public static final String FILE = "File";
        public static final String TASK = "Task";
    }
}
