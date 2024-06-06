package com.ercanbeyen.bankingapplication.integration.repository;

import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {
    @Container
    @ServiceConnection
    private final static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));
    @Autowired
    private CustomerRepository customerRepository;

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        mySQLContainer.start();
    }

    @Test
    @DisplayName("Happy path test: Save customer case")
    void givenCustomerEntity_whenSaveCustomer_thenCustomerIsPersisted() {
        Customer customer = new Customer();
        customer.setName("Test-Name1");
        customer.setSurname("Test-Surname1");
        customer.setNationalId("12345678911");
        customer.setEmail("test1@email.com");
        customer.setPhoneNumber("+905328465701");
        customer.setGender(Gender.MALE);
        customer.setBirthDate(LocalDate.of(2005, 8, 15));

        customerRepository.save(customer);

        Optional<Customer> retrievedCustomer = customerRepository.findById(1);
        assertTrue(retrievedCustomer.isPresent());
        assertEquals(customer.getName(), retrievedCustomer.get().getName());
    }
}
