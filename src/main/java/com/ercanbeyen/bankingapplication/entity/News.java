package com.ercanbeyen.bankingapplication.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract sealed class News extends BaseEntity permits BankNews, OfferNews {
    private String title;
    private String url;
}
