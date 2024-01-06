package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.City;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public final class AddressDto extends BaseDto {
    private City city;
    @NotNull(message = "Zip code should not be null")
    @Range(min = 10_000, max = 99_999, message = "Zip code is not between {min} and {max}")
    private Integer zipCode;
    @NotBlank(message = "Details should not be blank")
    @Size(min = 5, max = 200, message = "Length of details is not between {min} and {max}")
    private String details;
}
