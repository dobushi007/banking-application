package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ExchangeDto extends BaseDto {
    @NotNull(message = "Target currency should not be null")
    private Currency targetCurrency;
    @NotNull(message = "Base currency should not be null")
    private Currency baseCurrency;
    @Min(value = 1, message = "Minimum rate is {value}")
    private Double rate;
    @Range(min = 1, max = 100, message = "Buy percentage should between {min} and {max}")
    private Double buyPercentage;
    @Range(min = 1, max = 100, message = "Sell percentage should between {min} and {max}")
    private Double sellPercentage;
}
