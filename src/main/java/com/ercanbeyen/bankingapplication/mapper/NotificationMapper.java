package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.NotificationDto;
import com.ercanbeyen.bankingapplication.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "customerNationalId", source = "customer.nationalId")
    NotificationDto entityToDto(Notification notification);
    Notification dtoToEntity(NotificationDto notificationDto);
}
