package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.repository.BaseRepository;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ercanbeyen.bankingapplication.mapper.CustomerMapper.CUSTOMER_MAPPER;

@Service
@RequiredArgsConstructor
public class CustomerService implements BaseService<CustomerDto> {
    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerDto> findAll() {
        List<CustomerDto> customerDtoList = new ArrayList<>();

        customerRepository.findAll()
                .forEach(customer -> customerDtoList.add(CUSTOMER_MAPPER.customerToDto(customer)));

        return customerDtoList;
    }

    @Override
    public Optional<CustomerDto> findById(Integer id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerOptional.map(CUSTOMER_MAPPER::customerToDto);
    }

    @Override
    public CustomerDto create(CustomerDto request) {
        Customer customer = CUSTOMER_MAPPER.dtoToCustomer(request);
        return CUSTOMER_MAPPER.customerToDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto update(Integer id, CustomerDto request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer is not found"));

        customer.setName(request.getName());
        customer.setSurname(customer.getSurname());

        return CUSTOMER_MAPPER.customerToDto(customerRepository.save(customer));
    }

    @Override
    public void delete(Integer id) {
        customerRepository.deleteById(id);
    }
}
