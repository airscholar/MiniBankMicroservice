package com.airscholar.AccountService.command.api.controller;

import com.airscholar.AccountService.command.api.dto.TransactionDto;
import com.airscholar.AccountService.command.api.service.AccountService;
import com.airscholar.AccountService.data.Account;
import com.airscholar.BankService.command.api.command.DepositMoneyCommand;
import com.airscholar.BankService.command.api.command.WithdrawMoneyCommand;
import com.airscholar.BankService.command.api.data.JournalEntry;
import com.airscholar.CommonService.enums.TransactionType;
import com.airscholar.CommonService.queries.bank.GetTransactionsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@Slf4j
public class TransactionCommandController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final AccountService accountService;

    public TransactionCommandController(CommandGateway commandGateway, QueryGateway queryGateway, AccountService accountService) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.accountService = accountService;
    }

    @PatchMapping("/{accountId}/deposit")
    public String deposit(@PathVariable String accountId, @RequestBody TransactionDto transactionDto) {
        Account account = null;
        // get account
        try {
            account = accountService.validateAccount(accountId, transactionDto.getAmount(), TransactionType.CREDIT);
        } catch (Exception e) {
            log.error("Error while validating account: {}", e.getMessage());
            return e.getMessage();
        }

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
    public String withdraw( @PathVariable String accountId, @RequestBody TransactionDto transactionDto) {
        Account account = null;

        // get account
        try {
            account = accountService.validateAccount(accountId, transactionDto.getAmount(), TransactionType.DEBIT);
        } catch (Exception e) {
            log.error("Error while validating account: {}", e.getMessage());
            return e.getMessage();
        }

        WithdrawMoneyCommand withdrawMoneyCommand = WithdrawMoneyCommand.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(account.getAccountId())
                .amount(transactionDto.getAmount())
                .transactionType(String.valueOf(TransactionType.DEBIT))
                .build();
        log.info("Sending withdraw command {}", withdrawMoneyCommand);
        commandGateway.sendAndWait(withdrawMoneyCommand);

        return "Money Withdrawn";
    }
}
