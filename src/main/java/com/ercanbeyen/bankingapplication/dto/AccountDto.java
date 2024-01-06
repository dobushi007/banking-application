package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.City;
import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public non-sealed class AccountDto extends BaseDto {
    @NotBlank(message = "National identity should not be blank")
    @Pattern(regexp = "\\d{11}", message = "Length of national identity is not 11")
    private String customerNationalId;
    private City branchLocation;
    private Currency currency;
    @NotNull(message = "Balance should not be null")
    @Min(value = 0, message = "Minimum balance should be {value}")
    private Double balance;
    private AccountType type;
    /* Deposit Account fields */
    @Range(min = 0, max = 100, message = "Interest is not between {min} and {max}")
    private Double interest;
    private Integer depositPeriod;
}
