package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.embeddable.RegularTransfer;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "regular_transfer_orders")
public non-sealed class RegularTransferOrder extends BaseEntity {
    private Integer id;
    @ManyToOne
    private Account account;
    private Integer period;
    @Embedded
    @AttributeOverride(name = "receiverAccountId", column = @Column(name = "receiver_account_id"))
    private RegularTransfer regularTransfer;
}
