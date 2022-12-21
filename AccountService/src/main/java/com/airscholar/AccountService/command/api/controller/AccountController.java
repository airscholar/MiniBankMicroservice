package com.airscholar.AccountService.command.api.controller;

import com.airscholar.AccountService.command.api.commands.CreateAccountCommand;
import com.airscholar.AccountService.command.api.dto.AccountDto;
import com.airscholar.AccountService.command.api.dto.TransactionDto;
import com.airscholar.AccountService.data.Account;
import com.airscholar.AccountService.data.AccountRepository;
import com.airscholar.BankService.command.api.command.DepositMoneyCommand;
import com.airscholar.CommonService.enums.TransactionType;
import com.airscholar.CommonService.command.WithdrawMoneyCommand;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final AccountRepository accountRepository;

    public AccountController(CommandGateway commandGateway, QueryGateway queryGateway, AccountRepository accountRepository) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.accountRepository = accountRepository;
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

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{accountId}")
    public Account getAccountById(@PathVariable String accountId) {
        return accountRepository.findByAccountId(accountId);
    }

    @PostMapping("/{accountId}/deposit")
    public String deposit(@PathVariable String accountId, @RequestBody TransactionDto transactionDto) throws Exception {
        Account account = null;
        // get account
        try {
            account = validateAccount(accountId, transactionDto.getAmount(), TransactionType.CREDIT);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        log.info("Account found: {}", account);
        DepositMoneyCommand depositMoneyCommand = DepositMoneyCommand.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(account.getAccountId())
                .transactionType(String.valueOf(TransactionType.CREDIT))
                .amount(transactionDto.getAmount())
                .build();

        commandGateway.sendAndWait(depositMoneyCommand);

        return "Money Deposited";
    }

    @PatchMapping("/{accountId}/withdraw")
    public String withdraw( @PathVariable String accountId, @RequestBody Double amount) throws Exception {
        Account account = null;

        try {
            account = validateAccount(accountId, amount, TransactionType.DEBIT);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        WithdrawMoneyCommand withdrawMoneyCommand = WithdrawMoneyCommand.builder()
                .accountId(account.getAccountId())
                .amount(amount)
                .build();

        commandGateway.sendAndWait(withdrawMoneyCommand);

        return "Money Withdrawn";
    }

    private Account validateAccount(String accountId, Double amount, TransactionType transactionType) throws Exception {
        Account account = accountRepository.findByAccountId(accountId);

        // check if account exists
        if (account == null) {
            throw new Exception("Account not found");
        }

        switch (transactionType){
            case CREDIT:
                if (account.getAccountBalance() + amount > account.getCreditLimit()) {
                    throw new Exception("Credit limit exceeded");
                }
                break;
            case DEBIT:
                if (account.getAccountBalance() - amount < account.getOverdraftLimit()) {
                    throw new Exception("Overdraft limit exceeded");
                }
                break;
        }

        return account;
    }
//    @PatchMapping("/{accountId}/withdraw")
//    public String withdraw(@PathVariable String accountId, @RequestBody double amount) throws Exception {
//        Account account = null;
//        // get account
//        try {
//            account = accountRepository.find


}
