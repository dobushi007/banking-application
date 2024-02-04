package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.embeddable.RegularTransfer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public non-sealed class RegularTransferOrderDto extends BaseDto {
    private Integer senderAccountId;
    @NotNull(message = "Period should not be null")
    @Min(value = 1, message = "Period must be at least {value} week")
    private Integer period;
    @Valid
    private RegularTransfer regularTransfer;
}
