package com.ercanbeyen.bankingapplication.util;

import java.lang.reflect.Method;

public class LoggingUtils {
    public static String getClassName(Object object) {
        return object.getClass().getSimpleName();
    }

    public static String getMethodName(Method method) {
        return method.getName();
    }
}
