package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name = "customers")
public non-sealed class Customer extends BaseEntity {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String surname;
    @Getter
    @Setter
    @Column(name = "phone_number")
    private String phoneNumber;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Getter
    @Setter
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Address address;
    @Setter
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private File profilePhoto;

    public Optional<File> getProfilePhoto() {
        return Optional.ofNullable(profilePhoto);
    }
}
