package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto entityToDto(Customer customer);
    Customer dtoToEntity(CustomerDto customerDto);
}
