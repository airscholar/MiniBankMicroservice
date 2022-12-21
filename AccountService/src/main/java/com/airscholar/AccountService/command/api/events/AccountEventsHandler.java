package com.airscholar.AccountService.command.api.events;

import com.airscholar.AccountService.command.api.service.AccountService;
import com.airscholar.AccountService.data.Account;
import com.airscholar.BankService.command.api.events.CompleteBalanceUpdateEvent;
import com.airscholar.BankService.command.api.events.CancelDepositEvent;
import com.airscholar.CommonService.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class AccountEventsHandler {

    private final AccountService accountService;

    public AccountEventsHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    @EventHandler
    public void on(AccountCreatedEvent event) {
        Account account = new Account();
        BeanUtils.copyProperties(event, account);

        accountService.createAccount(account);
    }

    @EventHandler
    public void on(CompleteBalanceUpdateEvent event) {
        Account account = accountService.getAccountByAccountId(event.getAccountId());

        if (Objects.equals(event.getTransactionType(), String.valueOf(TransactionType.CREDIT))) {
            account.setAccountBalance(account.getAccountBalance() + event.getAmount());
        } else if (Objects.equals(event.getTransactionType(), String.valueOf(TransactionType.DEBIT))) {
            account.setAccountBalance(account.getAccountBalance() - event.getAmount());
        }

        accountService.updateAccount(account);
    }

    @EventHandler
    public void on(CancelDepositEvent event){
        log.info("CancelDepositEvent: from account service {}", event);
        Account account = accountService.getAccountByAccountId(event.getAccountId());
        account.setAccountBalance(account.getAccountBalance()-event.getTransactionAmount());
        accountService.updateAccount(account);
    }
}
