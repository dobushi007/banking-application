package com.ercanbeyen.bankingapplication.util;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;

@UtilityClass
public class StatisticsUtils {
    public <T> Map<T, Integer> getFrequencies(List<T> elements, Integer minimumFrequency) {
        return elements.stream()
                .collect(groupingBy(
                        Function.identity(),
                        collectingAndThen(Collectors.counting(), Long::intValue)))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= minimumFrequency)
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (value1, value2) -> value1,
                        LinkedHashMap::new
                ));
    }
}
