package com.ercanbeyen.bankingapplication.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MoneyTransferRequest(
        @NotNull(message = "Sender account id should not be null")
        Integer senderId,
        @NotNull(message = "Receiver account id should not be null")
        Integer receiverId,
        @Min(value = 1, message = "Minimum amount is {value}")
        Double amount,
        @NotNull(message = "Transfer date should not be null")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate transferDate,
        @Size(max = 500, message = "Maximum length for explanation is {max} characters")
        String explanation) {

}
