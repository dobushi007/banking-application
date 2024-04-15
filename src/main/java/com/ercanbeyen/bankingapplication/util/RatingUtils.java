package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.Month;

@UtilityClass
public class RatingUtils {
    private static final Month startMonth = Month.FEBRUARY;

    public static void checkRatingTime() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        if (startMonth.getValue() > now.getMonth().getValue()) {
            String message = String.format("%d ratings start in %s and ends in the end of the year", currentYear, startMonth);
            throw new ResourceConflictException(message);
        }
    }
}
