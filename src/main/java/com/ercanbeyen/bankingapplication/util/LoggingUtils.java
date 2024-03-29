package com.ercanbeyen.bankingapplication.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;

@UtilityClass
public final class LoggingUtils {

    public static String getClassName(Object object) {
        return object.getClass().getSimpleName();
    }

    public static String getMethodName(Method method) {
        return method.getName();
    }
}
