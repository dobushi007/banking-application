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
    @Column(name = "create_time", columnDefinition = Queries.GET_NOW_TIMESTAMP)
    private LocalDateTime createTime;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", columnDefinition = Queries.GET_NOW_TIMESTAMP)
    private LocalDateTime updateTime;
}
