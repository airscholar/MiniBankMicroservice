package com.airscholar.AccountService.command.api.aggregate;

import com.airscholar.AccountService.command.api.commands.CreateAccountCommand;
import com.airscholar.AccountService.command.api.events.AccountCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class AccountAggregate {

    @AggregateIdentifier
    private String transactionId;
    private String accountId;
    private String accountName;
    private Double accountBalance;
    private Double creditLimit;
    private long overdraftLimit;

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        log.info("CreateAccountCommand: {}", createAccountCommand);

        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent();

        BeanUtils.copyProperties(createAccountCommand, accountCreatedEvent);

        apply(accountCreatedEvent);
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent accountCreatedEvent) {
        this.accountId = accountCreatedEvent.getAccountId();
        this.transactionId = accountCreatedEvent.getTransactionId();
        this.accountName = accountCreatedEvent.getAccountName();
        this.accountBalance = accountCreatedEvent.getAccountBalance();
        this.creditLimit = accountCreatedEvent.getCreditLimit();
        this.overdraftLimit = accountCreatedEvent.getOverdraftLimit();
    }
}