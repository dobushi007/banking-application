package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.constant.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.LocalDateTime;

@Getter
@Entity(name = "transaction_view")
@Immutable
@Subselect("""
           SELECT t.id, t.type, a.currency, t.amount, t.sender_account_id, t.receiver_account_id, t.created_at
           FROM (transactions t
                 INNER JOIN accounts a ON (t.sender_account_id = a.id OR t.receiver_account_id = a.id))
           GROUP BY t.id, a.currency
           ORDER BY t.created_at DESC, t.amount DESC
           """
)
@Synchronize({"transactions", "accounts"})
public class TransactionView {
    @Id
    private String id;
    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Column
    private Double amount;
    @Column(name = "sender_account_id")
    private Integer senderAccountId;
    @Column(name = "receiver_account_id")
    private Integer receiverAccountId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
