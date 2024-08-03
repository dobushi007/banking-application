package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.*;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.AccountMapper;
import com.ercanbeyen.bankingapplication.mapper.CustomerMapper;
import com.ercanbeyen.bankingapplication.mapper.NotificationMapper;
import com.ercanbeyen.bankingapplication.mapper.RegularTransferOrderMapper;
import com.ercanbeyen.bankingapplication.option.AccountFilteringOptions;
import com.ercanbeyen.bankingapplication.option.CustomerFilteringOptions;
import com.ercanbeyen.bankingapplication.option.AccountActivityFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import com.ercanbeyen.bankingapplication.service.BaseService;
import com.ercanbeyen.bankingapplication.service.FileStorageService;
import com.ercanbeyen.bankingapplication.service.AccountActivityService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements BaseService<CustomerDto, CustomerFilteringOptions> {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AccountMapper accountMapper;
    private final RegularTransferOrderMapper regularTransferOrderMapper;
    private final NotificationMapper notificationMapper;
    private final FileStorageService fileStorageService;
    private final AccountActivityService accountActivityService;

    @Override
    public List<CustomerDto> getEntities(CustomerFilteringOptions options) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Predicate<Customer> customerPredicate = customer -> {
            Boolean addressCondition = (options.getCity() == null || options.getCity() == customer.getAddress().getCity());

            LocalDate filteringDay = options.getBirthDate();
            LocalDate customerBirthday = customer.getBirthDate();
            Boolean birthDayCondition = (filteringDay == null)
                    || (filteringDay.getMonth() == customerBirthday.getMonth() && filteringDay.getDayOfMonth() == customerBirthday.getDayOfMonth());

            Boolean createTimeCondition = (options.getCreateTime() == null || options.getCreateTime().isEqual(options.getCreateTime()));

            return (addressCondition && birthDayCondition && createTimeCondition);
        };

        List<CustomerDto> customerDtos = new ArrayList<>();

        customerRepository.findAll()
                .stream()
                .filter(customerPredicate)
                .forEach(customer -> customerDtos.add(customerMapper.entityToDto(customer)));

        return customerDtos;
    }

    @Override
    public CustomerDto getEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        return customerMapper.entityToDto(customer);
    }

    @Override
    public CustomerDto createEntity(CustomerDto request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        checkCustomerUniqueness(request.getNationalId(), request.getPhoneNumber(), request.getEmail());
        log.info(LogMessages.RESOURCE_UNIQUE, Entity.CUSTOMER.getValue());

        Customer customer = customerMapper.dtoToEntity(request);

        Customer savedCustomer = customerRepository.save(customer);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.CUSTOMER.getValue(), savedCustomer.getId());

        return customerMapper.entityToDto(savedCustomer);
    }

    @Transactional
    @Override
    public CustomerDto updateEntity(Integer id, CustomerDto request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        checkCustomerUniqueness(request.getNationalId(), request.getPhoneNumber(), request.getEmail());
        log.info(LogMessages.RESOURCE_UNIQUE, Entity.CUSTOMER.getValue());

        Customer requestCustomer = customerMapper.dtoToEntity(request);

        customer.setName(requestCustomer.getName());
        customer.setSurname(requestCustomer.getSurname());
        customer.setPhoneNumber(requestCustomer.getPhoneNumber());
        customer.setEmail(requestCustomer.getEmail());
        customer.setGender(requestCustomer.getGender());
        customer.setBirthDate(requestCustomer.getBirthDate());
        customer.setAddress(requestCustomer.getAddress());

        return customerMapper.entityToDto(customerRepository.save(customer));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        customerRepository.delete(customer);
    }

    public String uploadProfilePhoto(Integer id, MultipartFile file) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        CompletableFuture<File> photo = fileStorageService.storeFile(file);
        customer.setProfilePhoto(photo.join()); // Profile photo upload
        customerRepository.save(customer);

        return ResponseMessages.FILE_UPLOAD_SUCCESS;
    }

    public File downloadProfilePhoto(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        return customer.getProfilePhoto()
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));
    }

    public String deleteProfilePhoto(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        customer.setProfilePhoto(null); // Profile photo deletion
        customerRepository.save(customer);

        return ResponseMessages.FILE_DELETE_SUCCESS;
    }

    public List<AccountDto> getAccountsOfCustomer(Integer id, AccountFilteringOptions options) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        Predicate<Account> accountPredicate = account -> (account.getCustomer().getNationalId().equals(customer.getNationalId()))
                && (options.getType() == null || options.getType() == account.getType())
                && (options.getCreateTime() == null || options.getCreateTime().getYear() <= account.getCreatedAt().getYear());

        Comparator<Account> accountComparator = Comparator.comparing(Account::getCreatedAt).reversed();

        List<Account> accounts = customer.getAccounts()
                .stream()
                .filter(accountPredicate)
                .sorted(accountComparator)
                .toList();

        List<AccountDto> accountDtos = new ArrayList<>();
        accounts.forEach(account -> accountDtos.add(accountMapper.entityToDto(account)));

        return accountDtos;
    }

    public List<AccountActivityDto> getAccountActivitiesOfCustomer(Integer id, AccountActivityFilteringOptions options) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        List<AccountActivityDto> accountActivityDtos = new ArrayList<>();

        List<Integer> accountIds = customer.getAccounts()
                .stream()
                .map(Account::getId)
                .toList();

        /* Get all transactions of each account */
        accountIds.forEach(accountId -> {
            getAccountActivitiesOfCustomer(accountId, true, options, accountActivityDtos);
            getAccountActivitiesOfCustomer(accountId, false, options, accountActivityDtos);
        });

        Comparator<AccountActivityDto> transactionDtoComparator = Comparator.comparing(AccountActivityDto::createdAt).reversed();

        return accountActivityDtos.stream()
                .sorted(transactionDtoComparator)
                .toList();
    }

    public List<NotificationDto> getNotifications(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        List<NotificationDto> notificationDtos = new ArrayList<>();

        customer.getNotifications()
                .forEach(notification -> notificationDtos.add(notificationMapper.entityToDto(notification)));

        return notificationDtos;
    }

    public List<RegularTransferOrderDto> getRegularTransferOrdersOfCustomer(Integer customerId, Integer accountId) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = findById(customerId);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        Account account = customer.getAccount(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.ACCOUNT.getValue())));
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        return account.getRegularTransferOrders()
                .stream()
                .map(regularTransferOrderMapper::entityToDto)
                .toList();
    }

    /**
     *
     * @param nationalId is national identity which is unique for each customer
     * @return customer corresponds to that nationalId
     */
    public Customer findByNationalId(String nationalId) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());
        return customerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.CUSTOMER.getValue())));
    }

    /***
     *
     * @param nationalId is national identity which is unique for each customer
     * @return status for customer existence corresponds to nationalId
     */
    public boolean existsByNationalId(String nationalId) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());
        return customerRepository.existsByNationalId(nationalId);
    }

    private Customer findById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.CUSTOMER.getValue())));
    }

    private void getAccountActivitiesOfCustomer(Integer accountId, boolean isSender, AccountActivityFilteringOptions options, List<AccountActivityDto> accountActivityDtos) {
        AccountActivityFilteringOptions accountActivityFilteringOptions = isSender ?
                new AccountActivityFilteringOptions(options.type(), accountId, null, options.minimumAmount(), options.createAt()) :
                new AccountActivityFilteringOptions(options.type(), null, accountId, options.minimumAmount(), options.createAt());
        List<AccountActivityDto> currentAccountActivityDtos = accountActivityService.getAccountActivities(accountActivityFilteringOptions);
        accountActivityDtos.addAll(currentAccountActivityDtos);
    }

    private void checkCustomerUniqueness(String nationalId, String phoneNumber, String email) {
        Predicate<Customer> customerPredicate = customer -> customer.getNationalId().equals(nationalId)
                || customer.getPhoneNumber().equals(phoneNumber) || customer.getEmail().equals(email);
        boolean customerExists = customerRepository.findAll()
                .stream()
                .anyMatch(customerPredicate);

        if (customerExists) {
            throw new ResourceConflictException(String.format(ResponseMessages.ALREADY_EXISTS, Entity.CUSTOMER.getValue()));
        }
    }
}
