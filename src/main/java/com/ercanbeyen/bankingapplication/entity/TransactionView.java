package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Getter
@Entity(name = "transaction_view")
@Immutable
@Subselect(
        "SELECT t.id, t.type, t.amount, t.sender_account_id, t.receiver_account_id " +
        "FROM transactions t " +
        "GROUP BY t.id " +
        "ORDER BY t.id"
)
@Synchronize({"transactions"})
public class TransactionView {
    @Id
    private String id;
    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column
    private Double amount;
    @Column(name = "sender_account_id")
    private Integer senderAccountId;
    @Column(name = "receiver_account_id")
    private Integer receiverAccountId;
}
