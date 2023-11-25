package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.CustomerMapper;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements BaseService<CustomerDto> {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> findAll() {
        log.info("We are in findAll");
        List<CustomerDto> customerDtoList = new ArrayList<>();

        customerRepository.findAll()
                .forEach(customer -> customerDtoList.add(customerMapper.customerToDto(customer)));

        return customerDtoList;
    }

    @Override
    public Optional<CustomerDto> findById(Integer id) {
        log.info("We are in findById");
        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerOptional.map(customerMapper::customerToDto);
    }

    @Override
    public CustomerDto create(CustomerDto request) {
        log.info("We are in create");
        Customer customer = customerMapper.dtoToCustomer(request);
        return customerMapper.customerToDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto update(Integer id, CustomerDto request) {
        log.info("We are in update");
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer is not found"));

        customer.setName(request.getName());
        customer.setSurname(customer.getSurname());

        return customerMapper.customerToDto(customerRepository.save(customer));
    }

    @Override
    public void delete(Integer id) {
        log.info("We are in delete");
        customerRepository.deleteById(id);
    }
}
