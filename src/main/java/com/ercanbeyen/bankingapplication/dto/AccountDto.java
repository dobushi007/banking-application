package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.City;
import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Data
public non-sealed class AccountDto extends BaseDto {
    @NotBlank(message = "National identity should not be blank")
    @Pattern(regexp = "\\d{11}", message = "Length of national identity is not 11")
    private String customerNationalId;
    private City city;
    private Currency currency;
    private Double balance;
    private AccountType type;
    private LocalDateTime closedAt;
    /* Deposit Account fields */
    @Range(min = 0, max = 100, message = "Interest ratio is not between {min} and {max}")
    private Double interestRatio;
    private Integer depositPeriod;
}
