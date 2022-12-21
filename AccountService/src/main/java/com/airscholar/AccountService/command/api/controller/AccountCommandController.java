package com.airscholar.AccountService.command.api.controller;

import com.airscholar.AccountService.command.api.commands.CreateAccountCommand;
import com.airscholar.AccountService.command.api.dto.AccountDto;
import com.airscholar.AccountService.command.api.service.AccountService;
import com.airscholar.AccountService.data.Account;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountCommandController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final AccountService accountService;

    public AccountCommandController(CommandGateway commandGateway, QueryGateway queryGateway, AccountService accountService) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.accountService = accountService;
    }


    @PostMapping("/create")
    public String createAccount(@RequestBody AccountDto accountDto) {
        CreateAccountCommand createAccountCommand = CreateAccountCommand.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(UUID.randomUUID().toString())
                .accountName(accountDto.getAccountName())
                .accountBalance(accountDto.getAccountBalance())
                .overdraftLimit(accountDto.getOverdraftLimit())
                .creditLimit(accountDto.getCreditLimit())
                .build();

        commandGateway.sendAndWait(createAccountCommand);

        return "Account Created";
    }
}
