package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.NewsType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "news_report")
public non-sealed class NewsReport extends BaseEntity {
    private String title;
    private String url;
    @Enumerated(EnumType.STRING)
    private NewsType type;
}
