# Banking Application
---

## Spring Boot Application
---

### Summary
It is a banking application includes basic banking operations. Abstract crud approach is used throughout the development.<br/>
There are 3 entities in this project

Entities:
- Customer
- Address
- File

### Requirements
- Customer must have name, surname, phone number, email, gender, birth date and address.
- Customer's email must be unique.
- Address must have city, zip code and details.

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

### Run & Build
---
In order to pull the mysql image from the Dockerhub, you should run the below command

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

### Api Documentation
---

You may use swagger-ui with the port of the application to access the project's api documentation.<br/>
`http://localhost:${PORT}/swagger-ui.html`
