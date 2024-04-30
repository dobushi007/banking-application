package com.ercanbeyen.bankingapplication.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;

@UtilityClass
public class StatisticsUtils {
    public static <T> Map<T, Integer> getFrequencies(List<T> elements) {
        return elements.stream()
                .collect(groupingBy(Function.identity(),
                        collectingAndThen(Collectors.counting(), Long::intValue)));
    }
}
