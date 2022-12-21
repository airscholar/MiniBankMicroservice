package com.airscholar.AccountService.saga;

import com.airscholar.BankService.command.api.command.CompleteBalanceUpdateEvent;
import com.airscholar.BankService.command.api.command.UpdateBalanceCommand;
import com.airscholar.CommonService.command.CompleteDepositCommand;
import com.airscholar.CommonService.enums.TransactionStatus;
import com.airscholar.CommonService.enums.TransactionType;
import com.airscholar.CommonService.events.DepositMoneyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class AccountProcessingSaga {

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;

    @SagaEventHandler(associationProperty = "transactionId")
    @StartSaga
    public void handle(DepositMoneyCreatedEvent event) {
        log.info("DepositMoneyCreatedEvent started for transactionId: " + event.getTransactionId());

        UpdateBalanceCommand updateBalanceCommand = UpdateBalanceCommand.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .amount(event.getAmount())
                .transactionType(String.valueOf(TransactionType.CREDIT))
                .transactionStatus(String.valueOf(TransactionStatus.PENDING))
                .build();

        log.info("Sending DepositMoneyCreatedEvent to AccountService: {}", updateBalanceCommand);
        commandGateway.sendAndWait(updateBalanceCommand);

    }

    @SagaEventHandler(associationProperty = "transactionId")
    @EndSaga
    public void handle(CompleteBalanceUpdateEvent event) {
        log.info("CompleteBalanceUpdateEvent started for transactionId: " + event.getTransactionId());

        CompleteDepositCommand completeDepositCommand = CompleteDepositCommand.builder()
                .transactionId(event.getTransactionId())
                .transactionStatus(String.valueOf(TransactionStatus.COMPLETED))
                .build();

        log.info("Sending CompleteDepositCommand to BankService: {}", completeDepositCommand);
        commandGateway.sendAndWait(completeDepositCommand);

    }

}
