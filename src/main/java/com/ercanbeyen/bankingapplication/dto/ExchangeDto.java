package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ExchangeDto extends BaseDto {
    private Currency fromCurrency;
    private Currency toCurrency;
    @Min(value = 1, message = "Minimum rate is {value}")
    private Double rate;
    @Range(min = 1, max = 100, message = "Buy percentage should between {min} and {max}")
    private Double buyPercentage;
    @Range(min = 1, max = 100, message = "Sell percentage should between {min} and {max}")
    private Double sellPercentage;
}
