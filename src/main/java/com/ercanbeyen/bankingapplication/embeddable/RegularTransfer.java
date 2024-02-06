package com.ercanbeyen.bankingapplication.embeddable;

import com.ercanbeyen.bankingapplication.entity.Account;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RegularTransfer {
    @ManyToOne
    @JoinColumn(name = "receiver_account_id", referencedColumnName = "id")
    Account receiverAccount;
    Double amount;
    String explanation;
}
