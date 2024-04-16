package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.constant.enums.RatingReason;
import com.ercanbeyen.bankingapplication.dto.RatingDto;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.Month;

@UtilityClass
public class RatingUtils {
    private static final Month startMonth = Month.SEPTEMBER;
    private static final int THRESHOLD_RATE = 3;

    public static void checkRating(RatingDto ratingDto) {
        checkTime();
        checkReason(ratingDto);
    }

    private static void checkReason(RatingDto ratingDto) {
        RatingReason reason = RatingReason.OTHER;
        String message;
        RatingReason requestedReason = ratingDto.reason();

        if ((requestedReason == reason) && StringUtils.isBlank(ratingDto.explanation())) {
            message = String.format("Explanation of %s should not be blank or empty", reason);
            throw new ResourceExpectationFailedException(message);
        }

        int requestedRate = ratingDto.rate();
        reason = RatingReason.FINE;
        String template = "%s %s %s be used with rate more than %d";

        if (requestedRate <= THRESHOLD_RATE && requestedReason == reason) {
            message = String.format(template, "Reason", reason, "should", THRESHOLD_RATE);
            throw new ResourceExpectationFailedException(message);
        }

        if (requestedRate > THRESHOLD_RATE && requestedReason != reason) {
            message = String.format(template, "Only reason", reason, "must", THRESHOLD_RATE);
            throw new ResourceExpectationFailedException(message);
        }
    }

    private static void checkTime() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        if (startMonth.getValue() > now.getMonth().getValue()) {
            String message = String.format("%d ratings start in %s and ends in the end of the year", currentYear, startMonth);
            throw new ResourceConflictException(message);
        }
    }
}
