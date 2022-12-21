package com.airscholar.BankService.command.api.aggregate;

import com.airscholar.BankService.command.api.command.CompleteBalanceUpdateEvent;
import com.airscholar.BankService.command.api.command.UpdateBalanceCommand;
import com.airscholar.CommonService.command.CompleteDepositCommand;
import com.airscholar.BankService.command.api.command.DepositMoneyCommand;
import com.airscholar.CommonService.enums.TransactionStatus;
import com.airscholar.CommonService.events.CompleteDepositEvent;
import com.airscholar.CommonService.events.DepositMoneyCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class DepositAggregate {
    @AggregateIdentifier
    private String transactionId;
    private String accountId;
    private String transactionType;
    private String transactionDate;
    private Double transactionAmount;
    private String transactionStatus;

    @CommandHandler
    public DepositAggregate(DepositMoneyCommand depositMoneyCommand) {
        log.info("DepositMoneyCommand Started for transaction Id: {}", depositMoneyCommand.getTransactionId());

        DepositMoneyCreatedEvent depositMoneyCreatedEvent = DepositMoneyCreatedEvent.builder()
                .transactionId(depositMoneyCommand.getTransactionId())
                .accountId(depositMoneyCommand.getAccountId())
                .amount(depositMoneyCommand.getAmount())
                .transactionStatus(String.valueOf(TransactionStatus.CREATED))
                .transactionType(String.valueOf(depositMoneyCommand.getTransactionType()))
                .transactionDate(getCurrentDateTime())
                .build();

        log.info("DepositMoneyCreatedEvent : {}", depositMoneyCreatedEvent);

        apply(depositMoneyCreatedEvent);

        log.info("DepositMoneyCommand published for transaction Id: {}", depositMoneyCommand.getTransactionId());
    }

    private String getCurrentDateTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    @EventSourcingHandler
    public void on(DepositMoneyCreatedEvent event) {
        log.info("DepositMoneyCreatedEvent Started for transaction Id: {}", event.getTransactionId());
        this.accountId = event.getAccountId();
        this.transactionId = event.getTransactionId();
        this.transactionType = event.getTransactionType();
        this.transactionDate = event.getTransactionDate();
        this.transactionAmount = event.getAmount();
        this.transactionStatus = event.getTransactionStatus();
        log.info("DepositMoneyCreatedEvent Completed for transaction Id: {}", event);
    }

    @CommandHandler
    public void handle(UpdateBalanceCommand updateBalanceCommand) {
        log.info("UpdateBalanceCommand received: {}", updateBalanceCommand);

        CompleteBalanceUpdateEvent completeBalanceUpdateEvent = new CompleteBalanceUpdateEvent(
                updateBalanceCommand.getTransactionId(),
                updateBalanceCommand.getAccountId(),
                updateBalanceCommand.getAmount(),
                updateBalanceCommand.getTransactionStatus(),
                updateBalanceCommand.getTransactionType()
        );

        log.info("CompleteBalanceUpdateEvent published: {}", completeBalanceUpdateEvent);
        apply(completeBalanceUpdateEvent);

    }

    @EventSourcingHandler
    public void on(CompleteBalanceUpdateEvent completeBalanceUpdateEvent) {
        log.info("CompleteBalanceUpdateEvent received: {}", completeBalanceUpdateEvent);
        this.transactionId = completeBalanceUpdateEvent.getTransactionId();
        this.transactionAmount = completeBalanceUpdateEvent.getAmount();
        this.accountId = completeBalanceUpdateEvent.getAccountId();
        log.info("AccountUpdateAggregate updated: {}", this);
    }

    @CommandHandler
    public void handle(CompleteDepositCommand completeDepositCommand){
        CompleteDepositEvent completeDepositEvent = CompleteDepositEvent.builder()
                .transactionId(completeDepositCommand.getTransactionId())
                .transactionStatus(String.valueOf(TransactionStatus.COMPLETED))
                .build();

        apply(completeDepositEvent);
    }

    @EventSourcingHandler
    public void on(CompleteDepositEvent completeDepositEvent){
        this.transactionId = completeDepositEvent.getTransactionId();
        this.transactionStatus = String.valueOf(TransactionStatus.COMPLETED);
    }
}
