package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;

import java.time.LocalDate;
import java.util.function.Predicate;

public final class RegularTransferOrderUtils {
    private RegularTransferOrderUtils() {}

    public static Predicate<RegularTransferOrderDto> getRegularTransferOrderDtoPredicate() {
        /*
            Regular Transfer Date check flow:
            1) Increase transfer date adding by period until reaches to today date
            2) If next transfer date comes then it returns true, else it returns false
         */
        return regularTransferOrderDto -> {
            LocalDate nextTransferDate = regularTransferOrderDto.getCreatedAt().toLocalDate();
            LocalDate todayDate = LocalDate.now();

            do {
                nextTransferDate = nextTransferDate.plusWeeks(regularTransferOrderDto.getPeriod());
            } while (nextTransferDate.isBefore(todayDate));

            return todayDate.isEqual(nextTransferDate);
        };
    }
}
