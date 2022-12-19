package com.airscholar.AccountService.command.api.events;

import com.airscholar.AccountService.command.api.data.Account;
import com.airscholar.AccountService.command.api.data.AccountRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
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
}
