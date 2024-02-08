# Banking Application
---

## Spring Boot Application
---

### Summary
It is a banking application includes basic banking operations. Abstract crud approach is used throughout the development.<br/>
There are 6 entities in this project

Entities:
- Customer
- Account
- File
- Notification
- Transaction
- Regular Transfer Order

### Requirements
- Customer must have name, surname, national id, phone number, email, gender, birth date and address.
- Customer's national id, email and phone number must be unique.
- Account must have customer, type, branch location, currency and balance.
- If account is deposit, then it must also have deposit period and interest.
- Address must have city, zip code and details.
- Notification must have customer related national id and message.
- Transaction must have an amount and at least sender account id or receiver account id.
- Regular transfer order must have sender account, receiver account, period and amount.

### Additionals
- Scheduled tasks run while application is running.

### Tech Stack
---
- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Docker

### Prerequisites
---
- Maven
- Docker

### Build & Run & Debug
---
In order to pull mysql image from Dockerhub, you should run the below command

`$ docker pull mysql`

Then, you should run the below commands in order to run the application

1) Create jar file
2) Create the image of the application via building
3) Run the containers

```
$ mvn clean install
$ docker-compose build
$ docker-compose up
```

In order to debug the application, you should follow the below steps

1) Add Remote JVM Debug Configuration (You can use the default values that come in the debug configuration)
2) Run the containers
3) Start debugging on configured port (Default port is 5005)

### Api Documentation
---

You may use Swagger-UI with the port of the application you configured to access the project's api documentation.<br/>
You should use the below url to access the Swagger-UI. Default port is 8080.<br/>
`http://localhost:${PORT}/swagger-ui.html`
