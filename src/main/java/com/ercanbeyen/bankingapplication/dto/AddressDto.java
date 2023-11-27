package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.City;
import lombok.Data;

@Data
public final class AddressDto extends BaseDto {
    private City city;
    private Integer postCode;
    private String details;
}
