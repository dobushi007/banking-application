package com.ercanbeyen.bankingapplication.entity;


import com.ercanbeyen.bankingapplication.constant.enums.City;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "addresses")
public final class Address extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private City city;
    @Column(name = "zip_code")
    private Integer zipCode;
    private String details;
}
