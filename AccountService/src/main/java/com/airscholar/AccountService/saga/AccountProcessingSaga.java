package com.airscholar.AccountService.saga;

import com.airscholar.AccountService.command.api.dto.AccountDto;
import com.airscholar.AccountService.command.api.events.AccountCreatedEvent;
import com.airscholar.AccountService.data.Account;
import com.airscholar.AccountService.query.api.queries.GetAccountByAccountIdQuery;
import com.airscholar.BankService.command.api.command.DepositMoneyCommand;
import com.airscholar.BankService.command.api.events.CompleteBalanceUpdateEvent;
import com.airscholar.BankService.command.api.command.UpdateBalanceCommand;
import com.airscholar.CommonService.command.CancelDepositCommand;
import com.airscholar.CommonService.command.CompleteDepositCommand;
import com.airscholar.CommonService.enums.TransactionStatus;
import com.airscholar.CommonService.enums.TransactionType;
import com.airscholar.BankService.command.api.events.DepositMoneyCreatedEvent;
import com.airscholar.BankService.command.api.events.WithdrawMoneyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
@Slf4j
public class AccountProcessingSaga {

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;

    @SagaEventHandler(associationProperty = "accountId")
    @StartSaga
    public void handle(AccountCreatedEvent event){
        log.info("AccountCreatedEvent in Saga for Account Id : {}", event.getAccountId());

        //associate the accountId with transactionId
        String transactionId = UUID.randomUUID().toString();

        SagaLifecycle.associateWith("transactionId", transactionId);

        DepositMoneyCommand depositMoneyCommand = DepositMoneyCommand.builder()
                .accountId(event.getAccountId())
                .amount(event.getAccountBalance())
                .transactionId(transactionId)
                .transactionType(String.valueOf(TransactionType.CREDIT))
                .transactionStatus(String.valueOf(TransactionStatus.CREATED))
                .build();

        commandGateway.sendAndWait(depositMoneyCommand);
    }

    @SagaEventHandler(associationProperty = "transactionId")
    @StartSaga
    public void handle(DepositMoneyCreatedEvent event) {
        log.info("DepositMoneyCreatedEvent started for transactionId: " + event.getTransactionId());

        GetAccountByAccountIdQuery getAccountByAccountIdQuery = new GetAccountByAccountIdQuery(event.getAccountId());
        AccountDto accountDto = null;
        try {
            //it is important to get the account details
            accountDto = queryGateway.query(getAccountByAccountIdQuery, ResponseTypes.instanceOf(AccountDto.class)).join();

            if (accountDto == null) {
                log.error("Account not found for transactionId: " + event.getTransactionId());
                //start the compensating transaction
                cancelDepositCommand(event);
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            //Start the Compensating transaction
            cancelDepositCommand(event);
            return;
        }

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
    @StartSaga
    public void handle(WithdrawMoneyCreatedEvent event){
        log.info("WithdrawMoneyCreatedEvent started for transactionId: " + event.getTransactionId());

        GetAccountByAccountIdQuery getAccountByAccountIdQuery = new GetAccountByAccountIdQuery(event.getAccountId());
        AccountDto accountDto = null;
        try {
            //it is important to get the account details
            accountDto = queryGateway.query(getAccountByAccountIdQuery, ResponseTypes.instanceOf(AccountDto.class)).join();

            if (accountDto == null) {
                log.error("Account not found for transactionId: " + event.getTransactionId());
                //start the compensating transaction
                cancelWithdrawalCommand(event);
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            //Start the Compensating transaction
            cancelWithdrawalCommand(event);
            return;
        }

        UpdateBalanceCommand updateBalanceCommand = UpdateBalanceCommand.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .amount(event.getAmount())
                .transactionType(String.valueOf(TransactionType.DEBIT))
                .transactionStatus(String.valueOf(TransactionStatus.PENDING))
                .build();

        log.info("Sending WithdrawMoneyCreatedEvent to AccountService: {}", updateBalanceCommand);
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
    private void cancelDepositCommand(DepositMoneyCreatedEvent event) {
        CancelDepositCommand cancelDepositCommand = CancelDepositCommand.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .transactionAmount(event.getAmount())
                .transactionDate(event.getTransactionDate())
                .transactionStatus(String.valueOf(TransactionStatus.FAILED))
                .build();

        log.info("Sending CancelDepositCommand to BankService: {}", cancelDepositCommand);
        commandGateway.sendAndWait(cancelDepositCommand);
    }
    private void cancelWithdrawalCommand(WithdrawMoneyCreatedEvent event) {
        CancelDepositCommand cancelDepositCommand = CancelDepositCommand.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .transactionAmount(event.getAmount())
                .transactionDate(event.getTransactionDate())
                .transactionStatus(String.valueOf(TransactionStatus.FAILED))
                .build();

        log.info("Sending CancelDepositCommand to BankService: {}", cancelDepositCommand);
        commandGateway.sendAndWait(cancelDepositCommand);
    }
}
