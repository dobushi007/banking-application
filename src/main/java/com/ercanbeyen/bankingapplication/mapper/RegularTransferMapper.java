package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.RegularTransferDto;
import com.ercanbeyen.bankingapplication.embeddable.RegularTransfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegularTransferMapper {
    @Mapping(target = "receiverAccountId", source = "regularTransfer.receiverAccount.id")
    RegularTransferDto regularTransferToDto(RegularTransfer regularTransfer);
}
