package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @ManyToOne
    @JoinColumn(name = "sender_account_id", referencedColumnName = "id")
    private Account senderAccount;
    @ManyToOne
    @JoinColumn(name = "receiver_account_id", referencedColumnName = "id")
    private Account receiverAccount;
    private Double amount;
    private LocalDateTime createAt;
    private String explanation;

    public Transaction(TransactionType type, Account senderAccount, Account receiverAccount, Double amount, LocalDateTime createAt, String explanation) {
        this.type = type;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.createAt = createAt;
        this.explanation = explanation;
    }
}
