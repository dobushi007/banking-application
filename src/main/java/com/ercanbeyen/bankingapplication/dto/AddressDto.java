package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.City;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public final class AddressDto extends BaseDto {
    private City city;
    @NotNull(message = "Post code should not be null")
    private Integer postCode;
    @NotBlank(message = "Details should not be blank")
    private String details;
}
