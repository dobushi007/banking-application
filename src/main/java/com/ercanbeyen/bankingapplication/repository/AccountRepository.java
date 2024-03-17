package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.entity.Account;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends BaseRepository<Account> {
    @Procedure(name = "getTotalAccountsByCityAndTypeAndCurrency")
    Integer getTotalAccountsByCityAndTypeAndCurrency(
            @Param("city") String city,
            @Param("type") String type,
            @Param("currency") String currency
    );
}
