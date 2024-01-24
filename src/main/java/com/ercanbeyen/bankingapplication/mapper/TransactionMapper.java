package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.TransactionDto;
import com.ercanbeyen.bankingapplication.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "senderAccountId", source = "senderAccount.id")
    @Mapping(target = "receiverAccountId", source = "receiverAccount.id")
    TransactionDto transactionToDto(Transaction transaction);
    Transaction dtoToTransaction(TransactionDto transactionDto);
}
