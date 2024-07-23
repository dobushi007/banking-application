# Banking Application
---

## Spring Boot Application
---

### Summary
It is a banking application includes basic banking operations. Abstract crud approach is used throughout the development.<br/>
There are 9 entities in this project

Entities:
- Customer
- Account
- File
- Notification
- Transaction
- Regular Transfer Order
- News Report
- News
- Rating

### Requirements
- Customer's national id, email and phone number must be unique.
- If account is deposit, then it must also have deposit period and interest ratio.
- News Report is used to add news.
- Customers can rate the app between 1 and 5 once a year, starting in September.

### Additionals
- Scheduled tasks run while application is running.
- News are automatically added in chunks of size 40.
- Scripts inside resources/db.stored_procedure must be written into related database containers to call procedures.

### Tech Stack
---
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Data Cassandra
- Spring Batch
- JUnit 5
- Rest-assured
- MySQL
- Cassandra
- Docker

### Prerequisites
---
- Maven
- Docker

### Build & Run & Debug
---
In order to pull images from Dockerhub, you should run the below commands
```
$ docker pull mysql
$ docker pull cassandra
```

Then, you should run the below commands in order to run the application (Default port is 8080)

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

### Monitor
---
In order to monitor the application, you should use below actuator url to get metrics you may monitor.<br/>
`http://localhost:${PORT}/actuator`
 
If you want to monitor specific metric, you should append the metric to the actuator url. For example, in order to check health, you should use the below url to check health.<br/>
`http://localhost:${PORT}/actuator/health`

### Api Documentation
---
You may use Swagger-UI with the port of the application you configured to access the project's api documentation.<br/>
You should use the below url to access the Swagger-UI.<br/>
`http://localhost:${PORT}/swagger-ui.html`
