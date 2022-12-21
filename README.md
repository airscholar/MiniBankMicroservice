# Axon Mini Bank App
This is a sample mini bank application using Event Sourcing and CQRS. 
It is a simple application that allows you to create an account, deposit money and withdraw money.

## Functional Requirements
- [x] It should be possible to get a list of all transactions booked of a given bank account since a given calendar date.
- [x] It should be possible to test if a pending debit payment would exceed the overdraft limit of that bank account.
- [x] It should be possible to receive a list of all bank accounts in the red, i.e., whose account balance is lower than zero.
- [x] It should be possible to retrieve the current account balance of a given bank account.
- [x] It should be possible to open a bank account with an initial deposit and credit line. 


## Getting Started
[AccoountService](AccountService/src/main/java/com/airscholar/AccountService/AccountServiceApplication.java) handles account related requests application.

[BankService](BankService/src/main/java/com/airscholar/BankService/BankServiceApplication.java) handles transaction related requests application.

[CommonService](CommonService/src/main/java/com/airscholar/CommonService/CommonServiceApplication.java) is the library that contains all the common classes used by the other two applications.

## Running The App
To run the app, you need to run the following commands in the root directory of the project.

1. Run axon-server (more information [here](https://docs.axoniq.io/reference-guide/v/4.4/getting-started/running-axon-server))

```java
java -jar axondb-server.jar
```

2. Start the [AccountService](AccountService/src/main/java/com/airscholar/AccountService/AccountServiceApplication.java) application
3. Start the [BankService](BankService/src/main/java/com/airscholar/BankService/BankServiceApplication.java) application


## Useful links - Local Server
- [UI](http://localhost:9091)
- [API Documentation](http://localhost:9091/swagger-ui/index.html#/)
