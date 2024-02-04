package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;
import com.ercanbeyen.bankingapplication.entity.RegularTransferOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegularTransferOrderMapper {
    @Mapping(target = "senderAccountId", source = "regularTransferOrder.account.id")
    @Mapping(target = "regularTransfer.receiverAccountId", source = "regularTransferOrder.regularTransfer.receiverAccountId")
    @Mapping(target = "regularTransfer.explanation", source = "regularTransferOrder.regularTransfer.explanation")
    @Mapping(target = "regularTransfer.amount", source = "regularTransferOrder.regularTransfer.amount")
    RegularTransferOrderDto regularTransferOrderToDto(RegularTransferOrder regularTransferOrder);
}
