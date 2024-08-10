package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;
import com.ercanbeyen.bankingapplication.entity.RegularTransferOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegularTransferOrderMapper {
    @Mapping(target = "regularTransferDto.receiverAccountId", source = "regularTransferOrder.regularTransfer.receiverAccount.id")
    @Mapping(target = "regularTransferDto", source = "regularTransferOrder.regularTransfer")
    @Mapping(target = "senderAccountId", source = "regularTransferOrder.senderAccount.id")
    RegularTransferOrderDto entityToDto(RegularTransferOrder regularTransferOrder);
}
