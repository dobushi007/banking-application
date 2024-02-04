package com.ercanbeyen.bankingapplication.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RegularTransfer {
    Integer receiverAccountId;
    Double amount;
    String explanation;
}
