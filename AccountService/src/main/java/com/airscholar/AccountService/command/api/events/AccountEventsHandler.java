package com.airscholar.AccountService.command.api.events;

import com.airscholar.AccountService.data.AccountRepository;
import com.airscholar.AccountService.data.Account;
import com.airscholar.BankService.command.api.command.CompleteBalanceUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountEventsHandler {

    private final AccountRepository accountRepository;

    public AccountEventsHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent event) {
        Account account = new Account();
        BeanUtils.copyProperties(event, account);

        accountRepository.save(account);
    }

    @EventHandler
    public void on(CompleteBalanceUpdateEvent event) {
        log.info("CompleteBalanceUpdateEvent received for transactionId: {} and accountId: {}",
                event.getTransactionId(), event.getAccountId());
        Account account = accountRepository.findByAccountId(event.getAccountId());

        //check if the balance is going to go above credit limit
        if(account.getAccountBalance() + event.getAmount() > account.getCreditLimit()) {
            //TODO: throw error and start the compensation transaction
        }

        account.setAccountBalance(account.getAccountBalance()+event.getAmount()); ;
        log.info("Saving updated event in the DB: {}", account);
        accountRepository.save(account);
    }
}
