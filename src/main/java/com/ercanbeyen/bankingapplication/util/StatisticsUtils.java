package com.ercanbeyen.bankingapplication.util;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class StatisticsUtils {
    public static <T> Map<T, Integer> getFrequencies(List<T> elements, List<T> possibleElements) {
        Map<T, Integer> elementToOccurrence = new HashMap<>();

        for (T element : possibleElements) {
            int occurrence = Collections.frequency(elements, element);
            elementToOccurrence.put(element, occurrence);
        }

        return elementToOccurrence;
    }
}
