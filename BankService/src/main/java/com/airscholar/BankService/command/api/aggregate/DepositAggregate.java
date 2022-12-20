package com.airscholar.BankService.command.api.aggregate;

import com.airscholar.CommonService.command.CompleteDepositCommand;
import com.airscholar.CommonService.command.DepositMoneyCommand;
import com.airscholar.CommonService.enums.TransactionStatus;
import com.airscholar.CommonService.enums.TransactionType;
import com.airscholar.CommonService.events.CompleteDepositEvent;
import com.airscholar.CommonService.events.DepositMoneyCreatedEvent;
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
        this.accountId = event.getAccountId();
        this.transactionId = event.getTransactionId();
        this.transactionType = event.getTransactionType();
        this.transactionDate = event.getTransactionDate();
        this.transactionAmount = event.getAmount();
        this.transactionStatus = event.getTransactionStatus();
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
