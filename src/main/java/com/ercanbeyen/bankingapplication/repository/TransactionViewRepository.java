package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.entity.TransactionView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionViewRepository extends JpaRepository<TransactionView, String> {
    List<TransactionView> findBySenderAccountIdAndReceiverAccountId(Integer senderAccountId, Integer receiverAccountId);
}
