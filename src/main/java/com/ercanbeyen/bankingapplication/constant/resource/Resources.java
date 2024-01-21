package com.ercanbeyen.bankingapplication.constant.resource;

public class Resources {
    public static class EntityNames {
        public static final String CUSTOMER = "Customer";
        public static final String ACCOUNT = "Account";
        public static final String FILE = "File";
    }

    public static class Urls {
        private static final String HOST = "http://localhost:8080/api/v1";
        public static final String CUSTOMERS = HOST + "/customers";
        public static final String ACCOUNTS = HOST + "/accounts";
        public static final String NOTIFICATIONS = HOST + "/notifications";
    }
}
