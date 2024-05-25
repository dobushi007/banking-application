package com.ercanbeyen.bankingapplication.integration.controller;

import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.service.impl.CustomerService;
import com.ercanbeyen.bankingapplication.factory.MockCustomerFactory;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Slf4j
class CustomerControllerTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @Container
    @ServiceConnection
    static CassandraContainer<?> cassandraContainer = new CassandraContainer<>(DockerImageName.parse("cassandra:latest"));

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> String.format("jdbc:mysql://localhost:%d/test", mySQLContainer.getFirstMappedPort()));
        registry.add("spring.datasource.username", () -> "localhost");
        registry.add("spring.datasource.password", () -> "password");
    }

    @DynamicPropertySource
    static void registerCassandraProperties(DynamicPropertyRegistry registry) {
        cassandraContainer.start();

        registry.add("spring.data.cassandra.contactpoints", () -> cassandraContainer.getHost() + ":" + cassandraContainer.getFirstMappedPort());
        registry.add("spring.data.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.data.cassandra.port", cassandraContainer::getFirstMappedPort);
        registry.add("spring.data.cassandra.keyspace-name", () -> "mykeyspace");
        registry.add("spring.data.cassandra.entity-base-package", () -> "com.ercanbeyen.bankingapplication.entity");
        registry.add("spring.data.cassandra.username", () -> "cassandra");
        registry.add("spring.data.cassandra.password", () -> "cassandra");
    }

    @LocalServerPort
    private Integer port;

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void givenId_whenGetEntity_thenReturnCustomerDto() {
        CustomerDto customerDto = MockCustomerFactory.generateCustomerDtoRequest();
        CustomerDto createdCustomer = customerService.createEntity(customerDto);

        log.info("CustomerDto: {}", createdCustomer);

        given()
                .when()
                .get("api/v1/customers/{id}", 1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("nationalId", equalTo("12345678911"));
    }

    @Test
    void givenId_whenGetEntity_thenThrowResourceNotFoundException() {
        given()
                .when()
                .get("/api/v1/customers/{id}", 1)
                .then()
                .statusCode(404)
                .body("message", equalTo("Entity is not found"));

    }
}
