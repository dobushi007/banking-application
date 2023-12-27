package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.City;
import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public non-sealed class AccountDto extends BaseDto {
    private AccountType type;
    @NotBlank(message = "National identity should not be blank")
    @Pattern(regexp = "\\d{11}", message = "Length of national identity is not 11")
    private String customerNationalId;
    private City city;
    private Currency currency;
    @Min(value = 0, message = "Minimum balance should be zero")
    private Double balance;
    /* Deposit Account fields */
    @Range(min = 0, max = 100, message = "Interest is not between {min} and {max}")
    private Double interest;
    private Integer depositPeriod;
}
