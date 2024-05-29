package com.ercanbeyen.bankingapplication.unit.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.factory.MockCustomerFactory;
import com.ercanbeyen.bankingapplication.factory.MockFileFactory;
import com.ercanbeyen.bankingapplication.mapper.CustomerMapper;
import com.ercanbeyen.bankingapplication.option.CustomerFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import com.ercanbeyen.bankingapplication.service.impl.CustomerService;
import com.ercanbeyen.bankingapplication.service.impl.FileStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    public static final String TESTED_CLASS = "Customer Service";
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private FileStorageServiceImpl fileStorageService;

    private List<Customer> customers;
    private List<CustomerDto> customerDtos;

    @BeforeAll
    static void start() {
        log.info(LogMessages.Test.START, LogMessages.Test.UNIT, TESTED_CLASS);
    }

    @AfterAll
    static void end() {
        log.info(LogMessages.Test.END, LogMessages.Test.UNIT, TESTED_CLASS);
    }

    @BeforeEach
    void setUp() {
        log.info(LogMessages.Test.SETUP);
        customers = MockCustomerFactory.generateMockCustomers();
        customerDtos = MockCustomerFactory.generateMockCustomerDtos();
    }

    @AfterEach
    void tearDown() {
        log.info(LogMessages.Test.TEAR_DOWN);
    }

    @Test
    @DisplayName("Happy path test: Get customers case")
    void givenFilteringOptions_whenGetEntity_thenReturnCustomerDtos() {
        // given
        List<CustomerDto> expected = List.of(customerDtos.getFirst());
        CustomerFilteringOptions filteringOptions = new CustomerFilteringOptions();
        filteringOptions.setBirthDate(LocalDate.of(2005, 8, 15));

        doReturn(customers)
                .when(customerRepository)
                .findAll();
        doReturn(expected.getFirst())
                .when(customerMapper)
                .customerToDto(any());

        // when
        List<CustomerDto> actual = customerService.getEntities(filteringOptions);

        // then
        verify(customerRepository, times(1))
                .findAll();
        verify(customerMapper, times(1))
                .customerToDto(any());

        assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("Happy path test: Get customer case")
    void givenExistingId_whenGetEntity_thenReturnCustomerDto() {
        // given
        Optional<CustomerDto> expected = Optional.of(customerDtos.getFirst());
        Customer customer = customers.getFirst();

        doReturn(Optional.of(customer))
                .when(customerRepository)
                .findById(anyInt());
        doReturn(expected.get())
                .when(customerMapper)
                .customerToDto(any());

        // when
        Optional<CustomerDto> actual = customerService.getEntity(customer.getId());

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());
        verify(customerMapper, times(1))
                .customerToDto(any());

        Assumptions.assumeTrue(actual.isPresent());
        assertEquals(expected.get().getId(), actual.get().getId());
    }

    @Test
    @DisplayName("Happy path test: Get customer case")
    void givenNotExistingId_whenGetEntity_thenReturnEmptyOptionalCustomerDto() {
        // given
        doReturn(Optional.empty())
                .when(customerRepository)
                .findById(anyInt());

        // when
        Optional<CustomerDto> actual = customerService.getEntity(20);

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());
        verifyNoMoreInteractions(customerRepository, customerMapper);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Happy path test: Create customer case")
    void givenCustomerDto_whenCreateEntity_thenReturnCustomerDto() {
        // given
        Customer customer = customers.getFirst();
        CustomerDto expected = customerDtos.getFirst();
        CustomerDto request = MockCustomerFactory.generateCustomerDtoRequest();

        doReturn(customer)
                .when(customerMapper)
                .dtoToCustomer(any());
        doReturn(customer)
                .when(customerRepository)
                .save(any());
        doReturn(expected)
                .when(customerMapper)
                .customerToDto(any());

        // when
        CustomerDto actual = customerService.createEntity(request);

        // then
        verify(customerRepository, times(1))
                .findAll();
        verify(customerMapper, times(1))
                .dtoToCustomer(any());
        verify(customerRepository, times(1))
                .save(any());
        verify(customerMapper, times(1))
                .customerToDto(any());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Exception path test: Create customer case")
    void givenCustomerDto_whenCreateEntity_thenThrowResourceConflictException() {
        // given
        CustomerDto request = MockCustomerFactory.generateCustomerDtoRequest();
        String expected = String.format(ResponseMessages.ALREADY_EXISTS, Entity.CUSTOMER.getValue());

        doReturn(customers).when(customerRepository).findAll();

        // when
        RuntimeException exception = assertThrows(ResourceConflictException.class, () -> customerService.createEntity(request));
        String actual = exception.getMessage();

        // then
        verify(customerRepository, times(1))
                .findAll();
        verifyNoMoreInteractions(customerRepository, customerMapper);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@email.com", "test_updated@email.com"})
    @DisplayName("Happy path: Update customer case")
    void givenIdAndCustomerDto_whenUpdateEntity_thenReturnCustomerDto(String email) {
        // given
        CustomerDto request = getUpdateMockCustomerDtoRequest(email);

        Customer customer = customers.getFirst();
        customer.setEmail(email);

        doReturn(Optional.of(customers.getFirst()))
                .when(customerRepository)
                .findById(anyInt());
        doReturn(customers.getFirst())
                .when(customerMapper)
                .dtoToCustomer(any());
        doReturn(customer)
                .when(customerRepository)
                .save(any());
        doReturn(request)
                .when(customerMapper)
                .customerToDto(any());

        // when
        CustomerDto actual = customerService.updateEntity(customer.getId(), request);

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());
        verify(customerMapper, times(1))
                .dtoToCustomer(any());
        verify(customerRepository, times(1))
                .save(any());
        verify(customerMapper, times(1))
                .customerToDto(any());

        assertEquals(email, actual.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"test2@email.com", "test3@email.com"})
    @DisplayName("Exception path test: Update customer case")
    void givenIdAndCustomerDto_whenUpdateEntity_thenThrowResourceConflictException(String email) {
        // given
        CustomerDto request = getUpdateMockCustomerDtoRequest(email);
        String expected = String.format(ResponseMessages.ALREADY_EXISTS, Entity.CUSTOMER.getValue());

        doReturn(Optional.of(customers.getFirst()))
                .when(customerRepository)
                .findById(anyInt());
        doReturn(customers)
                .when(customerRepository)
                .findAll();

        // when
        RuntimeException exception = assertThrows(ResourceConflictException.class, () -> customerService.updateEntity(1, request));
        String actual = exception.getMessage();

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());
        verify(customerRepository, times(1))
                .findAll();
        verifyNoMoreInteractions(customerRepository, customerMapper);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Happy path test: Delete customer case")
    void givenExistingId_whenDeleteEntity_thenReturnNothing() {
        // given
        Customer customer = customers.getFirst();

        doReturn(Optional.of(customer))
                .when(customerRepository)
                .findById(anyInt());
        doNothing()
                .when(customerRepository)
                .delete(any());

        // when
        customerService.deleteEntity(customer.getId());

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());
        verify(customerRepository, times(1))
                .delete(any());
    }

    @Test
    @DisplayName("Exception path test: Delete customer case")
    void givenNotExistingId_whenDeleteEntity_thenThrowResourceNotFoundException() {
        // given
        String expected = String.format(ResponseMessages.NOT_FOUND, Entity.CUSTOMER.getValue());

        doReturn(Optional.empty())
                .when(customerRepository)
                .findById(anyInt());

        // when
        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> customerService.deleteEntity(20));
        String actual = exception.getMessage();

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());
        verifyNoMoreInteractions(customerRepository, customerMapper);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Happy path test: Upload photo case")
    void givenMultipartFile_whenUploadPhoto_thenReturnMessage() throws IOException {
        // given
        String expected = ResponseMessages.FILE_UPLOAD_SUCCESS;
        MultipartFile multipartFile = MockFileFactory.generateMockMultipartFile();
        File file = MockFileFactory.generateMockFile();
        CompletableFuture<File> fileCompletableFuture = CompletableFuture.supplyAsync(() -> file);

        doReturn(Optional.of(customers.getFirst()))
                .when(customerRepository)
                .findById(customers.getFirst().getId());
        doReturn(fileCompletableFuture)
                .when(fileStorageService)
                .storeFile(any());
        doReturn(customers.getFirst())
                .when(customerRepository)
                .save(any());

        // when
        String actual = customerService.uploadProfilePhoto(customers.getFirst().getId(), multipartFile);

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());
        verify(fileStorageService, times(1))
                .storeFile(any());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Exception path test: Upload photo case")
    void givenMultipartFile_whenUploadFile_thenThrowResourceExpectationFailedException() {
        // given
        String expected = ResponseMessages.FILE_UPLOAD_ERROR;
        MultipartFile multipartFile = MockFileFactory.generateMockMultipartFile();
        int id = 20;

        doReturn(Optional.of(customers.getFirst()))
                .when(customerRepository)
                .findById(anyInt());
        doThrow(new ResourceExpectationFailedException(ResponseMessages.FILE_UPLOAD_ERROR))
                .when(fileStorageService)
                .storeFile(any());

        // when
        RuntimeException exception = assertThrows(ResourceExpectationFailedException.class, () -> customerService.uploadProfilePhoto(id, multipartFile));
        String actual = exception.getMessage();

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());
        verify(fileStorageService, times(1))
                .storeFile(any());
        verifyNoMoreInteractions(customerRepository);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Happy path test: Download profile photo case")
    void givenId_whenDownloadProfilePhoto_thenReturnFile() throws IOException {
        // given
        File expected = MockFileFactory.generateMockFile();
        int id = customers.getFirst().getId();
        customers.getFirst().setProfilePhoto(expected);

        doReturn(Optional.of(customers.getFirst()))
                .when(customerRepository)
                .findById(anyInt());

        // when
        File actual = customerService.downloadProfilePhoto(id);

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());

        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    @DisplayName("Exception path test: Download profile photo")
    @Disabled(value = "This test is not increasing any coverage data")
    void givenId_whenDownloadProfilePhoto_thenThrowResourceNotFoundException() {
        // given
        String expected = String.format(ResponseMessages.NOT_FOUND, Entity.CUSTOMER.getValue());

        doReturn(Optional.empty())
                .when(customerRepository)
                .findById(anyInt());

        // when
        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> customerService.downloadProfilePhoto(20));
        String actual = exception.getMessage();

        // then
        verify(customerRepository, times(1))
                .findById(anyInt());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Happy path test: Delete profile photo case")
    void givenId_whenDeletePhoto_thenReturnMessage() {
        // given
        String expected = ResponseMessages.FILE_DELETE_SUCCESS;
        int id = customers.getFirst().getId();

        doReturn(Optional.of(customers.getFirst()))
                .when(customerRepository)
                .findById(anyInt());

        // when
        String actual = customerService.deleteProfilePhoto(id);

        // then
        verify(customerRepository, times(1)).findById(id);

        assertEquals(expected, actual);
    }

    private CustomerDto getUpdateMockCustomerDtoRequest(String email) {
        CustomerDto request = customerDtos.getFirst();
        request.setEmail(email);
        return request;
    }
}
