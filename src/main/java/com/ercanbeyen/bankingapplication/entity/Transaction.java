package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.TransactionType;
import com.ercanbeyen.bankingapplication.constant.query.Queries;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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
    @Column(name = "sender_account_id")
    private Integer senderAccountId;
    @Column(name = "receiver_account_id")
    private Integer receiverAccountId;
    private Double amount;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at", columnDefinition = Queries.GET_NOW_TIMESTAMP)
    private LocalDateTime createAt;
    private String explanation;

    public Transaction(TransactionType type, Integer senderAccountId, Integer receiverAccountId, Double amount, String explanation) {
        this.type = type;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.explanation = explanation;
    }
}
