package com.airscholar.AccountService.command.api.service;

import com.airscholar.AccountService.data.Account;
import com.airscholar.AccountService.data.AccountRepository;
import com.airscholar.CommonService.enums.TransactionType;

public class TransactionService {

    private final AccountRepository accountRepository;

    public TransactionService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


}
