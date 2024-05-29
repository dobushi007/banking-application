package com.ercanbeyen.bankingapplication.integration.controller;

import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerControllerTest {
    @Container
    @ServiceConnection
    private final static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));
    @Container
    @ServiceConnection
    private final static CassandraContainer<?> cassandraContainer = new CassandraContainer<>(DockerImageName.parse("cassandra:latest"));
    @LocalServerPort
    private Integer port;
    @Autowired
    private CustomerRepository customerRepository;

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        mySQLContainer.start();
    }

    @DynamicPropertySource
    static void registerCassandraProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.contactpoints", () -> cassandraContainer.getHost() + ":" + cassandraContainer.getFirstMappedPort());
        registry.add("spring.data.cassandra.local-datacenter", cassandraContainer::getLocalDatacenter);
        registry.add("spring.data.cassandra.port", cassandraContainer::getFirstMappedPort);
        registry.add("spring.data.cassandra.keyspace-name", () -> "mykeyspace");
        registry.add("spring.data.cassandra.entity-base-package", () -> "com.ercanbeyen.bankingapplication.entity");
        registry.add("spring.data.cassandra.username", cassandraContainer::getUsername);
        registry.add("spring.data.cassandra.password", cassandraContainer::getPassword);

        cassandraContainer.start();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterAll
    static void end() {
        mySQLContainer.stop();
        cassandraContainer.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Happy path test: Get customers case with no request parameter")
    void whenGetEntities_thenReturnCustomerDtos() {
        given()
                .when()
                .get("/api/v1/customers")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(0));
    }

    @Test
    @Order(2)
    @DisplayName("Happy path test: Create customer case")
    void givenCustomerDto_whenCreateEntity_thenReturnCustomerDto() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Test-Name1",
                            "surname": "Test-Surname1",
                            "nationalId": "12345678911",
                            "phoneNumber": "+905322864661",
                            "email": "test1@email.com",
                            "birthDate": "2005-08-15",
                            "gender": "MALE"
                        }
                        """)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("nationalId", equalTo("12345678911"));
    }

    @Test
    @Order(3)
    @DisplayName("Happy path test: Get customers case with birth date")
    void givenBirthDate_whenGetEntities_thenReturnCustomerDtos() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Test-Name2");
        newCustomer.setSurname("Test-Surname2");
        newCustomer.setNationalId("12345678912");
        newCustomer.setEmail("test2@email.com");
        newCustomer.setPhoneNumber("+905328465702");
        newCustomer.setGender(Gender.FEMALE);
        newCustomer.setBirthDate(LocalDate.of(2007, 4, 6));

        customerRepository.save(newCustomer);

        given()
                .queryParam("birthDate", "2003-08-15")
                .when()
                .get("/api/v1/customers")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(1));
    }

    @Test
    @Order(4)
    @DisplayName("Happy path test: Get customer case")
    void givenId_whenGetEntity_thenReturnCustomerDto() {
        given()
                .when()
                .get("/api/v1/customers/{id}", 1)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("nationalId", equalTo("12345678911"));
    }

    @Test
    @Order(5)
    @DisplayName("Exception path test: Get customer case")
    void givenId_whenGetEntity_thenThrowResourceNotFoundException() {
        given()
                .when()
                .get("/api/v1/customers/{id}", 25)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Entity is not found"));
    }

    @Test
    @Order(6)
    @DisplayName("Exception path test: Update customer case")
    void givenIdAndCustomerDto_whenUpdateEntity_thenThrowMethodArgumentNotValidException() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Test-Name1",
                            "surname": "Test-Surname1",
                            "nationalId": "12345678911",
                            "phoneNumber": "905322864662",
                            "email": "test1@email.com",
                            "birthDate": "2005-08-15",
                            "gender": "MALE"
                        }
                       """)
                .when()
                .put("/api/v1/customers/{id}", 1)
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("phoneNumber", equalTo("Invalid phone number"));

    }

    @Test
    @Order(7)
    @DisplayName("Happy path test: Delete customer case")
    void givenId_whenDeleteEntity_thenReturnMessage() {
        given()
                .when()
                .delete("/api/v1/customers/{id}", 1)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("response", equalTo(ResponseMessages.DELETE_SUCCESS));
    }
}
