package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
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

        Mockito.doReturn(customers).when(customerRepository).findAll();
        Mockito.doReturn(expected.getFirst()).when(customerMapper).customerToDto(Mockito.any());

        // when
        List<CustomerDto> actual = customerService.getEntities(filteringOptions);

        // then
        Mockito.verify(customerRepository, Mockito.times(1)).findAll();
        Mockito.verify(customerMapper, Mockito.times(1)).customerToDto(Mockito.any());

        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("Happy path test: Get customer case")
    void givenId_whenGetEntity_thenReturnCustomerDto() {
        // given
        Optional<CustomerDto> expected = Optional.of(customerDtos.getFirst());
        Customer customer = customers.getFirst();

        Mockito.doReturn(Optional.of(customer)).when(customerRepository).findById(customer.getId());
        Mockito.doReturn(expected.get()).when(customerMapper).customerToDto(customer);

        // when
        Optional<CustomerDto> actual = customerService.getEntity(customer.getId());

        // then
        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
        Mockito.verify(customerMapper, Mockito.times(1)).customerToDto(Mockito.any());

        Assumptions.assumeTrue(actual.isPresent());
        Assertions.assertEquals(expected.get().getId(), actual.get().getId());
    }

    @Test
    @DisplayName("Happy path test: Create customer case")
    void givenCustomerDto_whenCreateEntity_thenReturnCustomerDto() {
        // given
        Customer customer = customers.getFirst();
        CustomerDto expected = customerDtos.getFirst();

        CustomerDto request = new CustomerDto();
        request.setId(1);
        request.setName("Test-Name");
        request.setSurname("Test-Surname");
        request.setNationalId("12345678911");
        request.setEmail("test1@email.com");
        request.setGender(Gender.MALE);
        request.setPhoneNumber("+905328465703");

        Mockito.doReturn(customer).when(customerMapper).dtoToCustomer(request);
        Mockito.doReturn(customer).when(customerRepository).save(Mockito.any());
        Mockito.doReturn(expected).when(customerMapper).customerToDto(Mockito.any());

        // when
        CustomerDto actual = customerService.createEntity(request);

        // then
        Mockito.verify(customerRepository, Mockito.times(1)).findAll();
        Mockito.verify(customerMapper, Mockito.times(1)).dtoToCustomer(Mockito.any());
        Mockito.verify(customerRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(customerMapper, Mockito.times(1)).customerToDto(Mockito.any());

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@email.com", "test_updated@email.com"})
    @DisplayName("Happy path: Update customer case")
    void givenCustomerDto_whenUpdateEntity_thenReturnCustomerDto(String email) {
        // given
        CustomerDto request = customerDtos.getFirst();
        request.setEmail(email);

        Customer customer = customers.getFirst();
        customer.setEmail(email);

        Mockito.doReturn(Optional.of(customers.getFirst())).when(customerRepository).findById(customer.getId());
        Mockito.doReturn(customers.getFirst()).when(customerMapper).dtoToCustomer(request);
        Mockito.doReturn(customer).when(customerRepository).save(Mockito.any());
        Mockito.doReturn(request).when(customerMapper).customerToDto(Mockito.any());

        // when
        CustomerDto actual = customerService.updateEntity(customer.getId(), request);

        // then
        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
        Mockito.verify(customerMapper, Mockito.times(1)).dtoToCustomer(Mockito.any());
        Mockito.verify(customerRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(customerMapper, Mockito.times(1)).customerToDto(Mockito.any());

        Assertions.assertEquals(email, actual.getEmail());
    }

    @Test
    @DisplayName("Happy path test: Delete customer case")
    void givenId_whenDeleteEntity_thenReturnNothing() {
        // given
        Customer customer = customers.getFirst();

        Mockito.doReturn(Optional.of(customer)).when(customerRepository).findById(customer.getId());
        Mockito.doNothing().when(customerRepository).delete(Mockito.any());

        // when
        customerService.deleteEntity(customer.getId());

        // then
        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
        Mockito.verify(customerRepository, Mockito.times(1)).delete(Mockito.any());
    }
}
