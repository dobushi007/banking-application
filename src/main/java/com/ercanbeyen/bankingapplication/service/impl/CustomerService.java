package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;
import com.ercanbeyen.bankingapplication.dto.TransactionDto;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.AccountMapper;
import com.ercanbeyen.bankingapplication.mapper.CustomerMapper;
import com.ercanbeyen.bankingapplication.mapper.RegularTransferOrderMapper;
import com.ercanbeyen.bankingapplication.option.AccountFilteringOptions;
import com.ercanbeyen.bankingapplication.option.CustomerFilteringOptions;
import com.ercanbeyen.bankingapplication.option.TransactionFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import com.ercanbeyen.bankingapplication.service.BaseService;
import com.ercanbeyen.bankingapplication.service.FileStorageService;
import com.ercanbeyen.bankingapplication.service.TransactionService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final FileStorageService fileStorageService;
    private final TransactionService transactionService;

    @Override
    public List<CustomerDto> getEntities(CustomerFilteringOptions options) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

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
                .forEach(customer -> customerDtos.add(customerMapper.customerToDto(customer)));

        return customerDtos;
    }

    @Override
    public Optional<CustomerDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Optional<Customer> customerOptional = customerRepository.findById(id);

        return customerOptional.map(customerMapper::customerToDto);
    }

    @Override
    public CustomerDto createEntity(CustomerDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        checkCustomerUniqueness(request.getNationalId(), request.getPhoneNumber());
        log.info(LogMessages.RESOURCE_UNIQUE, Entity.CUSTOMER.getValue());

        Customer customer = customerMapper.dtoToCustomer(request);

        return customerMapper.customerToDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto updateEntity(Integer id, CustomerDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Customer customer = findCustomerById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        Customer requestCustomer = customerMapper.dtoToCustomer(request);

        customer.setName(requestCustomer.getName());
        customer.setSurname(requestCustomer.getSurname());
        customer.setPhoneNumber(requestCustomer.getPhoneNumber());
        customer.setEmail(requestCustomer.getEmail());
        customer.setGender(requestCustomer.getGender());
        customer.setBirthDate(requestCustomer.getBirthDate());
        customer.setAddress(requestCustomer.getAddress());

        return customerMapper.customerToDto(customerRepository.save(customer));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Customer customer = findCustomerById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        customerRepository.delete(customer);
    }

    public String uploadProfilePhoto(Integer id, MultipartFile file) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Customer customer = findCustomerById(id);
        CompletableFuture<File> photo = fileStorageService.storeFile(file);
        customer.setProfilePhoto(photo.join()); // Profile photo upload
        customerRepository.save(customer);

        return ResponseMessages.FILE_UPLOAD_SUCCESS;
    }

    public File downloadProfilePhoto(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Customer customer = findCustomerById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        return customer.getProfilePhoto()
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));
    }

    public String deleteProfilePhoto(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Customer customer = findCustomerById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        customer.setProfilePhoto(null); // Profile photo deletion
        customerRepository.save(customer);

        return ResponseMessages.FILE_DELETE_SUCCESS;
    }

    public List<AccountDto> getAccountsOfCustomer(Integer id, AccountFilteringOptions options) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Customer customer = findCustomerById(id);
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
        accounts.forEach(account -> accountDtos.add(accountMapper.accountToDto(account)));

        return accountDtos;
    }

    public List<TransactionDto> getTransactionsOfCustomer(Integer id, TransactionFilteringOptions options) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Customer customer = findCustomerById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        List<TransactionDto> transactionDtos = new ArrayList<>();

        List<Integer> accountIds = customer.getAccounts()
                .stream()
                .map(Account::getId)
                .toList();

        /* Get all transactions of each account */
        accountIds.forEach(accountId -> {
            getTransactionsOfCustomer(accountId, true, options, transactionDtos);
            getTransactionsOfCustomer(accountId, false, options, transactionDtos);
        });

        Comparator<TransactionDto> transactionDtoComparator = Comparator.comparing(TransactionDto::createdAt).reversed();

        return transactionDtos.stream()
                .sorted(transactionDtoComparator)
                .toList();
    }

    public List<RegularTransferOrderDto> getRegularTransferOrdersOfCustomer(Integer customerId, Integer accountId) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Customer customer = findCustomerById(customerId);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        Account account = customer.getAccount(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.ACCOUNT.getValue())));
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        return account.getRegularTransferOrders()
                .stream()
                .map(regularTransferOrderMapper::regularTransferOrderToDto)
                .toList();
    }

    /**
     *
     * @param nationalId is national identity which is unique for each customer
     * @return customer corresponds to that nationalId
     */
    public Customer findCustomerByNationalId(String nationalId) {
        return customerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.CUSTOMER.getValue())));
    }

    private Customer findCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.CUSTOMER.getValue())));
    }

    private void getTransactionsOfCustomer(Integer accountId, boolean isSender, TransactionFilteringOptions options, List<TransactionDto> transactionDtos) {
        TransactionFilteringOptions transactionFilteringOptions = isSender ?
                new TransactionFilteringOptions(options.type(), accountId, null, options.minimumAmount(), options.createAt()) :
                new TransactionFilteringOptions(options.type(), null, accountId, options.minimumAmount(), options.createAt());
        List<TransactionDto> currentTransactionDtos = transactionService.getTransactions(transactionFilteringOptions);
        transactionDtos.addAll(currentTransactionDtos);
    }

    private void checkCustomerUniqueness(String nationalId, String phoneNumber) {
        Predicate<Customer> customerPredicate = customer -> customer.getNationalId().equals(nationalId) || customer.getPhoneNumber().equals(phoneNumber);
        boolean customerExists = customerRepository.findAll()
                .stream()
                .anyMatch(customerPredicate);

        if (customerExists) {
            throw new ResourceConflictException(ResponseMessages.ALREADY_EXISTS);
        }
    }
}
