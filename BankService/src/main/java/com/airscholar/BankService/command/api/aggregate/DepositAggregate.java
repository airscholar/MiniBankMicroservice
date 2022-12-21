package com.airscholar.BankService.command.api.aggregate;

import com.airscholar.BankService.command.api.events.CompleteBalanceUpdateEvent;
import com.airscholar.BankService.command.api.command.UpdateBalanceCommand;
import com.airscholar.CommonService.command.CancelDepositCommand;
import com.airscholar.CommonService.command.CompleteDepositCommand;
import com.airscholar.BankService.command.api.command.DepositMoneyCommand;
import com.airscholar.BankService.command.api.command.WithdrawMoneyCommand;
import com.airscholar.CommonService.enums.TransactionStatus;
import com.airscholar.CommonService.enums.TransactionType;
import com.airscholar.BankService.command.api.events.CancelDepositEvent;
import com.airscholar.CommonService.events.CompleteDepositEvent;
import com.airscholar.CommonService.events.DepositMoneyCreatedEvent;
import com.airscholar.BankService.command.api.events.WithdrawMoneyCreatedEvent;
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

    @CommandHandler
    public DepositAggregate(WithdrawMoneyCommand withdrawMoneyCommand){
        log.info("WithdrawMoneyCommand Started for transaction Id: {}", withdrawMoneyCommand.getTransactionId());

        WithdrawMoneyCreatedEvent withdrawMoneyCreatedEvent = WithdrawMoneyCreatedEvent.builder()
                .transactionId(withdrawMoneyCommand.getTransactionId())
                .accountId(withdrawMoneyCommand.getAccountId())
                .amount(withdrawMoneyCommand.getAmount())
                .transactionStatus(String.valueOf(TransactionStatus.CREATED))
                .transactionType(String.valueOf(TransactionType.DEBIT))
                .transactionDate(getCurrentDateTime())
                .build();

        log.info("WithdrawMoneyCommand published for transaction Id: {}", withdrawMoneyCommand.getTransactionId());

        apply(withdrawMoneyCreatedEvent);
    }

    @CommandHandler
    public DepositAggregate(CancelDepositCommand cancelDepositCommand){
        log.info("CancelDepositCommand Started for transaction Id: {}", cancelDepositCommand.getTransactionId());

        CancelDepositEvent cancelDepositEvent = CancelDepositEvent.builder()
                .transactionId(cancelDepositCommand.getTransactionId())
                .transactionStatus(String.valueOf(TransactionStatus.FAILED))
                .transactionDate(cancelDepositCommand.getTransactionDate())
                .transactionAmount(cancelDepositCommand.getTransactionAmount())
                .accountId(cancelDepositCommand.getAccountId())
                .transactionType(cancelDepositCommand.getTransactionType())
                .build();
        log.info("CancelDepositEvent published for transaction Id: {}", cancelDepositCommand.getTransactionId());
        apply(cancelDepositEvent);
    }

    @CommandHandler
    public void handle(CompleteDepositCommand completeDepositCommand){
        log.info("CompleteDepositCommand received: {}", completeDepositCommand);

        CompleteDepositEvent completeDepositEvent = CompleteDepositEvent.builder()
                .transactionId(completeDepositCommand.getTransactionId())
                .transactionStatus(String.valueOf(TransactionStatus.COMPLETED))
                .build();

        apply(completeDepositEvent);
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

    @EventSourcingHandler
    public void on(WithdrawMoneyCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.transactionId = event.getTransactionId();
        this.transactionType = event.getTransactionType();
        this.transactionDate = event.getTransactionDate();
        this.transactionAmount = event.getAmount();
        this.transactionStatus = event.getTransactionStatus();
    }

    @EventSourcingHandler
    public void on(CancelDepositEvent event) {
        this.accountId = event.getAccountId();
        this.transactionId = event.getTransactionId();
        this.transactionType = event.getTransactionType();
        this.transactionDate = event.getTransactionDate();
        this.transactionAmount = event.getTransactionAmount();
        this.transactionStatus = event.getTransactionStatus();
    }


    @EventSourcingHandler
    public void on(CompleteBalanceUpdateEvent completeBalanceUpdateEvent) {
        log.info("CompleteBalanceUpdateEvent received: {}", completeBalanceUpdateEvent);
        this.transactionId = completeBalanceUpdateEvent.getTransactionId();
        this.accountId = completeBalanceUpdateEvent.getAccountId();
        this.transactionAmount = completeBalanceUpdateEvent.getAmount();
        this.accountId = completeBalanceUpdateEvent.getAccountId();
        this.transactionStatus = completeBalanceUpdateEvent.getTransactionStatus();
        this.transactionType = completeBalanceUpdateEvent.getTransactionType();
    }

    @EventSourcingHandler
    public void on(CompleteDepositEvent completeDepositEvent){
        this.transactionId = completeDepositEvent.getTransactionId();
        this.transactionStatus = String.valueOf(TransactionStatus.COMPLETED);
    }


}
