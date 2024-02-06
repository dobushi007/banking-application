package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import com.ercanbeyen.bankingapplication.embeddable.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Entity
@Table(name = "customers", indexes = {@Index(name = "nationalId_index", columnList = "nationalId")})
public non-sealed class Customer extends BaseEntity {
    @Getter
    @Column(nullable = false, length = 100)
    private String name;
    @Getter
    @Column(nullable = false, length = 100)
    private String surname;
    @Getter
    @Column(name = "national_id", nullable = false, length = 11, unique = true)
    private String nationalId;
    @Getter
    @Column(name = "phone_number", length = 13, unique = true)
    private String phoneNumber;
    @Getter
    @Column(nullable = false, unique = true)
    private String email;
    @Getter
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Getter
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Getter
    @Embedded
    @AttributeOverride(name = "city", column = @Column(name = "city"))
    @AttributeOverride(name = "zipCode", column = @Column(name = "zip_code"))
    @AttributeOverride(name = "details", column = @Column(name = "address_details", length = 500))
    private Address address;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "profile_photo")
    private File profilePhoto;
    @Getter
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();
    @Getter
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    public Optional<File> getProfilePhoto() {
        return Optional.ofNullable(profilePhoto);
    }
}
