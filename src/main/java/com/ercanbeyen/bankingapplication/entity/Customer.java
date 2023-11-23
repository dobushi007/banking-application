package com.ercanbeyen.bankingapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {
    @Column(name = "first_name")
    private String name;
    @Column(name = "last_name")
    private String surname;
}
