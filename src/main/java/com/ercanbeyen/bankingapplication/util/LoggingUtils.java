package com.ercanbeyen.bankingapplication.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggingUtils {
    private final String NO_ITEMS_IN_STACK_TRACE = "There are no %s in the stack trace";

    public String getCurrentClassName() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(stackFrameStream -> stackFrameStream.skip(1).findFirst())
                //.map(StackWalker.StackFrame::getClassName) --> works but not outputs simple name
                .map(stackFrame -> stackFrame.getDeclaringClass().getSimpleName()) // works
                .orElse(String.format(String.format(NO_ITEMS_IN_STACK_TRACE, "classes")));
    }

    public String getCurrentMethodName() {
        return StackWalker.getInstance()
                .walk(stackFrameStream -> stackFrameStream.skip(1).findFirst())
                //.walk(stackFrameStream -> stackFrameStream.skip(0).findFirst()) --> outputs getCurrentMethodName
                .map(StackWalker.StackFrame::getMethodName)
                .orElse(String.format(NO_ITEMS_IN_STACK_TRACE, "methods"));
    }
}
