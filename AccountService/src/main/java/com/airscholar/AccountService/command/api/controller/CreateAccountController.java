package com.airscholar.AccountService.command.api.controller;

import com.airscholar.AccountService.command.api.commands.CreateAccountCommand;
import com.airscholar.AccountService.command.api.data.AccountRepository;
import com.airscholar.AccountService.command.api.dto.AccountDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class CreateAccountController {

    private final CommandGateway commandGateway;
    private final AccountRepository accountRepository;

    public CreateAccountController(CommandGateway commandGateway, AccountRepository accountRepository) {
        this.commandGateway = commandGateway;
        this.accountRepository = accountRepository;
    }


    @PostMapping("/create")
    public String createAccount(@RequestBody AccountDto accountDto) {
        String accountId = UUID.randomUUID().toString();
        CreateAccountCommand createAccountCommand = CreateAccountCommand.builder()
                .accountId(accountId)
                .accountName(accountDto.getAccountName())
                .accountBalance(accountDto.getAccountBalance())
                .overdraftLimit(accountDto.getOverdraftLimit())
                .creditLimit(accountDto.getCreditLimit())
                .build();

        commandGateway.sendAndWait(createAccountCommand);

        return "Account Created";
    }

}
