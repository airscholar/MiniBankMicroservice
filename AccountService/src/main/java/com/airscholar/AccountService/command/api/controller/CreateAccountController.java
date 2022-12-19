package com.airscholar.AccountService.command.api.controller;

import com.airscholar.AccountService.command.api.commands.CreateAccountCommand;
import com.airscholar.AccountService.command.api.data.Account;
import com.airscholar.AccountService.command.api.data.AccountRepository;
import com.airscholar.AccountService.command.api.dto.AccountDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class CreateAccountController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final AccountRepository accountRepository;

    public CreateAccountController(CommandGateway commandGateway, QueryGateway queryGateway, AccountRepository accountRepository) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
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

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{accountId}")
    public Account getAccountById(@PathVariable String accountId) {
        return accountRepository.findById(accountId).get();
    }
}
