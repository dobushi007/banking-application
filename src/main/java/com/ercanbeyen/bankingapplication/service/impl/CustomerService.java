package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.AddressDto;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Address;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.AddressMapper;
import com.ercanbeyen.bankingapplication.mapper.CustomerMapper;
import com.ercanbeyen.bankingapplication.option.CustomerFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import com.ercanbeyen.bankingapplication.service.BaseService;
import com.ercanbeyen.bankingapplication.service.FileStorageService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements BaseService<CustomerDto, CustomerFilteringOptions> {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final AddressService addressService;
    private final FileStorageService fileStorageService;


    @Override
    public List<CustomerDto> getEntities(CustomerFilteringOptions options) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        List<CustomerDto> customerDtoList = new ArrayList<>();

        customerRepository.findAll()
                .forEach(customer -> customerDtoList.add(customerMapper.customerToDto(customer)));

        return customerDtoList;
    }

    @Override
    public Optional<CustomerDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Optional<Customer> customerOptional = customerRepository.findById(id);

        return customerOptional.map(customerMapper::customerToDto);
    }

    @Override
    public CustomerDto createEntity(CustomerDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        checkCustomerNationalId(request.getNationalId());

        Customer customer = customerMapper.dtoToCustomer(request);
        AddressDto addressDto = addressService.createEntity(request.getAddressDto());
        Address address = addressMapper.dtoToAddress(addressDto);

        customer.setAddress(address);

        return customerMapper.customerToDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto updateEntity(Integer id, CustomerDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Customer customer = findCustomerById(id);
        log.info(LogMessages.RESOURCE_FOUND, LogMessages.ResourceNames.CUSTOMER);

        Customer requestCustomer = customerMapper.dtoToCustomer(request);

        customer.setName(requestCustomer.getName());
        customer.setSurname(requestCustomer.getSurname());
        customer.setPhoneNumber(requestCustomer.getPhoneNumber());
        customer.setEmail(requestCustomer.getEmail());
        customer.setGender(requestCustomer.getGender());
        customer.setBirthDate(requestCustomer.getBirthDate());
        addressService.updateEntity(customer.getAddress().getId(), request.getAddressDto());

        return customerMapper.customerToDto(customerRepository.save(customer));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Customer customer = findCustomerById(id);
        log.info(LogMessages.RESOURCE_FOUND, LogMessages.ResourceNames.CUSTOMER);

        customerRepository.delete(customer);
    }

    public String uploadProfilePhoto(Integer id, MultipartFile file) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Customer customer = findCustomerById(id);

        File photo = fileStorageService.storeFile(file);
        customer.setProfilePhoto(photo); // Profile photo upload
        customerRepository.save(customer);

        return ResponseMessages.FILE_UPLOAD_SUCCESS;
    }

    public File downloadProfilePhoto(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Customer customer = findCustomerById(id);
        log.info(LogMessages.RESOURCE_FOUND, LogMessages.ResourceNames.CUSTOMER);

        return customer.getProfilePhoto()
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));
    }

    public String deleteProfilePhoto(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Customer customer = findCustomerById(id);

        customer.setProfilePhoto(null); // Profile photo deletion
        customerRepository.save(customer);

        return ResponseMessages.FILE_DELETE_SUCCESS;
    }

    /**
     *
     * @param nationalId is national identity which is unique for each customer
     * @return customer corresponds to that nationalId
     */
    public Customer findCustomerByNationalId(String nationalId) {
        return customerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));
    }

    private Customer findCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));
    }

    private void checkCustomerNationalId(String nationalId) {
        boolean customerExists = customerRepository.findAll()
                .stream()
                .map(Customer::getNationalId)
                .anyMatch(nationalId::equals);

        if (customerExists) {
            throw new ResourceConflictException(ResponseMessages.ALREADY_EXISTS);
        }
    }
}
