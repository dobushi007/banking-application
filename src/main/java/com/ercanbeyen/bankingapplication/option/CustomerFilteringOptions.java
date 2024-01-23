package com.ercanbeyen.bankingapplication.option;

import com.ercanbeyen.bankingapplication.constant.enums.City;
import lombok.Data;

import java.time.LocalDate;

@Data
public non-sealed class CustomerFilteringOptions extends BaseFilteringOptions {
    City city;
    LocalDate birthDate;
}
