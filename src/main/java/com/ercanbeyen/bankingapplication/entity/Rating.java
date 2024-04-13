package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.RatingReason;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(value = "ratings")
public class Rating {
    @PrimaryKey
    //@GeneratedValue(strategy = GenerationType.UUID)
    //private UUID id = UUID.randomUUID();
    private UUID id;
    private String userNationalId;
    /*@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)*/
    private LocalDateTime createdAt;
    /*@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)*/
    private LocalDateTime updatedAt;
    private Double rate;
    @Enumerated(EnumType.STRING)
    private RatingReason ratingReason;

   /* @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID();
    }*/
}
