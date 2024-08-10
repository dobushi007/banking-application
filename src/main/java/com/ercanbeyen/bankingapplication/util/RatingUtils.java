package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.constant.enums.RatingReason;
import com.ercanbeyen.bankingapplication.dto.RatingDto;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.Month;

@UtilityClass
@Slf4j
public class RatingUtils {
    private final Month START_MONTH = Month.SEPTEMBER;
    private final int THRESHOLD_RATE = 3;
    private final int START_YEAR = 1900;

    public void checkRatingStatisticsFilteringParameters(Integer fromYear, Integer toYear, Integer frequency) {
        checkFrequency(frequency);
        checkYears(fromYear, toYear);
    }

    public void checkRatingBeforeSave(RatingDto ratingDto) {
        checkTimeForCurrentYear();
        checkReason(ratingDto);
    }

    private void checkFrequency(Integer frequency) {
        if (frequency <= 0) {
            throw new ResourceExpectationFailedException("Frequency should be greater than zero");
        }
    }

    private void checkYears(Integer fromYear, Integer toYear) {
        if ((fromYear != null && toYear != null) && (fromYear > toYear)) {
            throw new ResourceExpectationFailedException("Start year must be less than or equal to end year");
        }

        checkYear(fromYear);
        checkYear(toYear);
    }

    private void checkYear(Integer year) {
        if (year == null) {
            log.warn("Year is null");
            return;
        }

        int currentYear = LocalDateTime.now().getYear();

        if (year < START_YEAR || year > currentYear) {
            throw new ResourceExpectationFailedException("Invalid year parameter");
        } else if (year == currentYear) {
            checkTimeForCurrentYear();
        }
    }

    private void checkReason(RatingDto ratingDto) {
        RatingReason reason = RatingReason.OTHER;
        String message;
        RatingReason requestedReason = ratingDto.reason();

        if (requestedReason == reason && StringUtils.isBlank(ratingDto.explanation())) {
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

    private void checkTimeForCurrentYear() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        if (START_MONTH.getValue() > now.getMonth().getValue()) {
            String message = String.format("%d ratings start in %s and ends in the end of the year", currentYear, START_MONTH);
            throw new ResourceConflictException(message);
        }
    }
}
