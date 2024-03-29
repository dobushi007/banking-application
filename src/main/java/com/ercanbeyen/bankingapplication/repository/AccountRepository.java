package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.City;
import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.response.StatisticsResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends BaseRepository<Account> {
    @Procedure(name = "getTotalAccountsByCityAndTypeAndCurrency")
    Integer getTotalAccountsByCityAndTypeAndCurrency(
            @Param("city") String city,
            @Param("type") String type,
            @Param("currency") String currency
    );

    @Query(value = """
                SELECT new com.ercanbeyen.bankingapplication.response.StatisticsResponse(
                    c.nationalId, CONCAT(c.name, ' ', c.surname), a.id, a.city, a.balance)
                FROM Customer c
                JOIN c.accounts a
                WHERE a.type = ?1 AND a.currency = ?2 AND a.balance = (
                    SELECT MAX(a1.balance)
                    FROM Account a1
                    WHERE a1.type = ?1 AND a1.currency = ?2
                )
                ORDER BY c.nationalId ASC
           """)
    List<StatisticsResponse> getCustomersHaveMaximumBalanceByTypeAndCurrency(
            AccountType type,
            Currency currency
    );

    @Query(value = """
                SELECT new com.ercanbeyen.bankingapplication.response.StatisticsResponse(
                    c.nationalId, CONCAT(c.name, ' ', c.surname), a.id, a.city, a.balance)
                FROM Customer c
                JOIN c.accounts a
                WHERE a.city = :city AND a.type = :type AND a.currency = :currency AND a.balance = (
                    SELECT MAX(a1.balance)
                    FROM Account a1
                    WHERE a1.city = :city AND a1.type = :type AND a1.currency = :currency
                )
                ORDER BY c.nationalId ASC, a.city ASC
           """)
    List<StatisticsResponse> getCustomersHaveMaximumBalanceByTypeAndCurrencyAndCity(
            @Param("type") AccountType type,
            @Param("currency") Currency currency,
            @Param("city") City city
    );

}
