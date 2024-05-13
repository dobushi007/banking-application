package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.factory.MockCustomerFactory;
import com.ercanbeyen.bankingapplication.mapper.CustomerMapper;
import com.ercanbeyen.bankingapplication.option.CustomerFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;

    private List<Customer> customers;
    private List<CustomerDto> customerDtos;

    @BeforeAll
    static void start() {
        log.info("Unit tests of Customer Service are starting");
    }

    @AfterAll
    static void end() {
        log.info("Unit tests of Customer Service are finishing");
    }

    @BeforeEach
    void setUp() {
        log.info("Setup...");
        customers = MockCustomerFactory.getMockCustomers();
        customerDtos = MockCustomerFactory.getMockCustomerDtos();
    }

    @AfterEach
    void tearDown() {
        log.info("Tear down...");
    }

    @Test
    @DisplayName("Happy path test: Get customers case")
    void givenFilteringOptions_whenGetEntity_thenReturnCustomerDtos() {
        // given
        List<CustomerDto> expected = List.of(customerDtos.getFirst());
        CustomerFilteringOptions filteringOptions = new CustomerFilteringOptions();
        filteringOptions.setBirthDate(LocalDate.of(2005, 8, 15));

        Mockito.doReturn(customers)
                .when(customerRepository)
                .findAll();
        Mockito.doReturn(expected.getFirst())
                .when(customerMapper)
                .customerToDto(Mockito.any());

        // when
        List<CustomerDto> actual = customerService.getEntities(filteringOptions);

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findAll();
        Mockito.verify(customerMapper, Mockito.times(1))
                .customerToDto(Mockito.any());

        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("Happy path test: Get customer case")
    void givenExistingId_whenGetEntity_thenReturnCustomerDto() {
        // given
        Optional<CustomerDto> expected = Optional.of(customerDtos.getFirst());
        Customer customer = customers.getFirst();

        Mockito.doReturn(Optional.of(customer))
                .when(customerRepository)
                .findById(Mockito.anyInt());
        Mockito.doReturn(expected.get())
                .when(customerMapper)
                .customerToDto(Mockito.any());

        // when
        Optional<CustomerDto> actual = customerService.getEntity(customer.getId());

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findById(Mockito.anyInt());
        Mockito.verify(customerMapper, Mockito.times(1))
                .customerToDto(Mockito.any());

        Assumptions.assumeTrue(actual.isPresent());
        Assertions.assertEquals(expected.get().getId(), actual.get().getId());
    }

    @Test
    @DisplayName("Happy path test: Get customer case")
    void givenNotExistingId_whenGetEntity_thenReturnEmptyOptionalCustomerDto() {
        // given
        Mockito.doReturn(Optional.empty())
                .when(customerRepository)
                .findById(Mockito.anyInt());

        // when
        Optional<CustomerDto> actual = customerService.getEntity(20);

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(customerRepository, customerMapper);

        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Happy path test: Create customer case")
    void givenCustomerDto_whenCreateEntity_thenReturnCustomerDto() {
        // given
        Customer customer = customers.getFirst();
        CustomerDto expected = customerDtos.getFirst();
        CustomerDto request = MockCustomerFactory.getCustomerDtoRequest();

        Mockito.doReturn(customer)
                .when(customerMapper)
                .dtoToCustomer(Mockito.any());
        Mockito.doReturn(customer)
                .when(customerRepository)
                .save(Mockito.any());
        Mockito.doReturn(expected)
                .when(customerMapper)
                .customerToDto(Mockito.any());

        // when
        CustomerDto actual = customerService.createEntity(request);

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findAll();
        Mockito.verify(customerMapper, Mockito.times(1))
                .dtoToCustomer(Mockito.any());
        Mockito.verify(customerRepository, Mockito.times(1))
                .save(Mockito.any());
        Mockito.verify(customerMapper, Mockito.times(1))
                .customerToDto(Mockito.any());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Exception path test: Create customer case")
    void givenCustomerDto_whenCreateEntity_thenThrowResourceConflictException() {
        // given
        CustomerDto request = MockCustomerFactory.getCustomerDtoRequest();
        String expected = String.format(ResponseMessages.ALREADY_EXISTS, Entity.CUSTOMER.getValue());

        Mockito.doReturn(customers).when(customerRepository).findAll();

        // when
        RuntimeException exception = Assertions.assertThrows(ResourceConflictException.class, () -> customerService.createEntity(request));
        String actual = exception.getMessage();

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findAll();
        Mockito.verifyNoMoreInteractions(customerRepository, customerMapper);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@email.com", "test_updated@email.com"})
    @DisplayName("Happy path: Update customer case")
    void givenCustomerDto_whenUpdateEntity_thenReturnCustomerDto(String email) {
        // given
        CustomerDto request = getUpdatedMockCustomerDtoRequest(email);

        Customer customer = customers.getFirst();
        customer.setEmail(email);

        Mockito.doReturn(Optional.of(customers.getFirst()))
                .when(customerRepository)
                .findById(Mockito.anyInt());
        Mockito.doReturn(customers.getFirst())
                .when(customerMapper)
                .dtoToCustomer(Mockito.any());
        Mockito.doReturn(customer)
                .when(customerRepository)
                .save(Mockito.any());
        Mockito.doReturn(request)
                .when(customerMapper)
                .customerToDto(Mockito.any());

        // when
        CustomerDto actual = customerService.updateEntity(customer.getId(), request);

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findById(Mockito.anyInt());
        Mockito.verify(customerMapper, Mockito.times(1))
                .dtoToCustomer(Mockito.any());
        Mockito.verify(customerRepository, Mockito.times(1))
                .save(Mockito.any());
        Mockito.verify(customerMapper, Mockito.times(1))
                .customerToDto(Mockito.any());

        Assertions.assertEquals(email, actual.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"test2@email.com", "test3@email.com"})
    @DisplayName("Exception path test: Update customer case")
    void givenCustomerDto_whenUpdateEntity_thenThrowResourceConflictException(String email) {
        // given
        CustomerDto request = getUpdatedMockCustomerDtoRequest(email);
        String expected = String.format(ResponseMessages.ALREADY_EXISTS, Entity.CUSTOMER.getValue());

        Mockito.doReturn(Optional.of(customers.getFirst()))
                .when(customerRepository)
                .findById(Mockito.anyInt());
        Mockito.doReturn(customers)
                .when(customerRepository)
                .findAll();

        // when
        RuntimeException exception = Assertions.assertThrows(ResourceConflictException.class, () -> customerService.updateEntity(1, request));
        String actual = exception.getMessage();

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findById(Mockito.anyInt());
        Mockito.verify(customerRepository, Mockito.times(1))
                .findAll();
        Mockito.verifyNoMoreInteractions(customerRepository, customerMapper);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Happy path test: Delete customer case")
    void givenExistingId_whenDeleteEntity_thenReturnNothing() {
        // given
        Customer customer = customers.getFirst();

        Mockito.doReturn(Optional.of(customer))
                .when(customerRepository)
                .findById(Mockito.anyInt());
        Mockito.doNothing()
                .when(customerRepository)
                .delete(Mockito.any());

        // when
        customerService.deleteEntity(customer.getId());

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findById(Mockito.anyInt());
        Mockito.verify(customerRepository, Mockito.times(1))
                .delete(Mockito.any());
    }

    private CustomerDto getUpdatedMockCustomerDtoRequest(String email) {
        CustomerDto request = customerDtos.getFirst();
        request.setEmail(email);
        return request;
    }

    @Test
    @DisplayName("Exception path test: Delete customer case")
    void givenNotExistingId_whenDeleteEntity_thenThrowResourceNotFoundException() {
        // given
        String expected = String.format(ResponseMessages.NOT_FOUND, Entity.CUSTOMER.getValue());

        Mockito.doReturn(Optional.empty())
                .when(customerRepository)
                .findById(Mockito.anyInt());

        // when
        RuntimeException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.deleteEntity(20));
        String actual = exception.getMessage();

        // then
        Mockito.verify(customerRepository, Mockito.times(1))
                .findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(customerRepository, customerMapper);

        Assertions.assertEquals(expected, actual);
    }
}
