package com.ercanbeyen.bankingapplication.entity;


import com.ercanbeyen.bankingapplication.constant.query.Queries;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract sealed class BaseEntity permits Account, Customer, NewsReport, RegularTransferOrder, News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = Queries.GET_NOW_TIMESTAMP)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = Queries.GET_NOW_TIMESTAMP)
    private LocalDateTime updatedAt;
}
