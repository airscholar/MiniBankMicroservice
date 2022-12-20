package com.airscholar.AccountService.command.api.aggregate;

import com.airscholar.AccountService.command.api.events.CompleteBalanceUpdateEvent;
import com.airscholar.CommonService.command.UpdateBalanceCommand;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateAggregate {
    @AggregateIdentifier
    private String transactionId;
    private String accountId;
    private String accountName;
    private Double accountBalance;
    private Double creditLimit;
    private long overdraftLimit;

    @CommandHandler
    public void AccountUpdateAggregate(UpdateBalanceCommand updateBalanceCommand) {
        log.info("UpdateBalanceCommand received: {}", updateBalanceCommand);

        CompleteBalanceUpdateEvent completeBalanceUpdateEvent = CompleteBalanceUpdateEvent.builder()
                .transactionId(updateBalanceCommand.getTransactionId())
                .accountId(updateBalanceCommand.getAccountId())
                .amount(updateBalanceCommand.getAmount())
                .transactionStatus(updateBalanceCommand.getTransactionStatus())
                .transactionType(updateBalanceCommand.getTransactionType())
                .build();
        log.info("CompleteBalanceUpdateEvent sent: {}", completeBalanceUpdateEvent);
        apply(completeBalanceUpdateEvent);
    }

    @EventSourcingHandler
    public void on(CompleteBalanceUpdateEvent completeBalanceUpdateEvent) {
        log.info("CompleteBalanceUpdateEvent received: {}", completeBalanceUpdateEvent);
        this.transactionId = completeBalanceUpdateEvent.getTransactionId();
        this.accountBalance = completeBalanceUpdateEvent.getAmount();
        this.accountId = completeBalanceUpdateEvent.getAccountId();
        log.info("AccountUpdateAggregate updated: {}", this);
    }
}
