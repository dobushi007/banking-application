package com.ercanbeyen.bankingapplication.constant.message;

public class LogMessages {
    public static final String ECHO_MESSAGE = "Program is in class {} and method {}";
    public static final String RESOURCE_FOUND = "{} is found";
    public static final String RESOURCE_NOT_FOUND = "{} is not found";
    public static final String EXCEPTION_MESSAGE = "Exception message: {}";
    public static final String TRANSACTION_MESSAGE = "Transaction message: {}";

    public static class ResourceNames {
        public static final String CUSTOMER = "Customer";
        public static final String ACCOUNT = "Account";
        public static final String ADDRESS = "Address";
        public static final String FILE = "File";
    }
}
