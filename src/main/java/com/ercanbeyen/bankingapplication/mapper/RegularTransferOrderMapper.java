package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;
import com.ercanbeyen.bankingapplication.entity.RegularTransferOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegularTransferOrderMapper {
    @Mapping(target = "senderAccountId", source = "regularTransferOrder.senderAccount.id")
    RegularTransferOrderDto regularTransferOrderToDto(RegularTransferOrder regularTransferOrder);
}
