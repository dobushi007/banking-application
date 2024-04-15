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
    private UUID id;
    @Column(name = "user_national_id")
    private String userNationalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer year;
    private Double rate;
    @Enumerated(EnumType.STRING)
    private RatingReason reason;
    private String explanation;
}
