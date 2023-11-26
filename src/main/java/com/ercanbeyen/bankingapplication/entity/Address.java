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
    @Column(name = "post_code")
    private Integer postCode;
    private String details;
}
