package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "addressDto", source = "address")
    CustomerDto customerToDto(Customer customer);
    @Mapping(target = "address", source = "addressDto")
    Customer dtoToCustomer(CustomerDto customerDto);
}
