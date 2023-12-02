package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.constant.names.BaseMethods;
import com.ercanbeyen.bankingapplication.constant.names.ClassNames;
import com.ercanbeyen.bankingapplication.dto.AddressDto;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Address;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.AddressMapper;
import com.ercanbeyen.bankingapplication.mapper.CustomerMapper;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import com.ercanbeyen.bankingapplication.service.BaseService;
import com.ercanbeyen.bankingapplication.service.FileStorageService;
import com.ercanbeyen.bankingapplication.util.PhotoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements BaseService<CustomerDto> {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final AddressService addressService;
    private final FileStorageService fileStorageService;

    @Override
    public List<CustomerDto> getEntities() {
        log.info(LogMessages.ECHO_MESSAGE, ClassNames.CUSTOMER_SERVICE, BaseMethods.GET_ENTITIES);
        List<CustomerDto> customerDtoList = new ArrayList<>();

        customerRepository.findAll()
                .forEach(customer -> customerDtoList.add(customerMapper.customerToDto(customer)));

        return customerDtoList;
    }

    @Override
    public Optional<CustomerDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO_MESSAGE, ClassNames.CUSTOMER_SERVICE, BaseMethods.GET_ENTITY);
        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerOptional.map(customerMapper::customerToDto);
    }

    @Override
    public CustomerDto createEntity(CustomerDto request) {
        log.info(LogMessages.ECHO_MESSAGE, ClassNames.CUSTOMER_SERVICE, BaseMethods.CREATE_ENTITY);
        Customer customer = customerMapper.dtoToCustomer(request);

        AddressDto addressDto = addressService.createEntity(request.getAddressDto());
        Address address = addressMapper.dtoToAddress(addressDto);
        customer.setAddress(address);

        return customerMapper.customerToDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto updateEntity(Integer id, CustomerDto input) {
        log.info(LogMessages.ECHO_MESSAGE, ClassNames.CUSTOMER_SERVICE, BaseMethods.UPDATE_ENTITY);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));

        Customer requestCustomer = customerMapper.dtoToCustomer(input);

        customer.setName(requestCustomer.getName());
        customer.setSurname(requestCustomer.getSurname());
        customer.setPhoneNumber(requestCustomer.getPhoneNumber());
        customer.setEmail(requestCustomer.getEmail());
        customer.setGender(requestCustomer.getGender());
        customer.setBirthDate(requestCustomer.getBirthDate());
        addressService.updateEntity(customer.getAddress().getId(), input.getAddressDto());

        return customerMapper.customerToDto(customerRepository.save(customer));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO_MESSAGE, ClassNames.CUSTOMER_SERVICE, BaseMethods.DELETE_ENTITY);
        customerRepository.deleteById(id);
    }

    public String uploadPhoto(Integer id, MultipartFile file) throws IOException {
        log.info(LogMessages.ECHO_MESSAGE, ClassNames.CUSTOMER_SERVICE, "uploadPhoto");
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));

        PhotoUtils.checkPhoto(file);
        log.info("control checkPhoto is passed");

        File photo = fileStorageService.storeFile(file);
        customer.setPhoto(photo);
        customerRepository.save(customer);

        return "Uploaded the file successfully: " + file.getOriginalFilename();
    }

    public File downloadPhoto(Integer id) {
        log.info(LogMessages.ECHO_MESSAGE, "customerService", "downloadPhoto");
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));
        return customer.getPhoto();
    }
}
