package com.airscholar.AccountService.command.api.service;

import com.airscholar.AccountService.command.api.dto.AccountBalanceDto;
import com.airscholar.AccountService.command.api.dto.AccountDto;
import com.airscholar.AccountService.data.Account;
import com.airscholar.AccountService.data.AccountRepository;
import com.airscholar.AccountService.query.api.queries.GetAccountByAccountIdQuery;
import com.airscholar.CommonService.enums.TransactionType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    public Account getAccountByAccountId(String accountId) {
        return accountRepository.findByAccountId(accountId);
    }

    public Account getAccountBalanceByAccountId(String accountId) {
        return getAccountByAccountId(accountId);
    }

    public List<AccountBalanceDto> getAllRedAccounts() {
        List<Account> accounts = accountRepository.findAll().stream().filter(account -> account.getAccountBalance() < 0).collect(Collectors.toList());

        return accounts.stream().map(account -> {
            AccountBalanceDto accountBalanceDto = new AccountBalanceDto();
            BeanUtils.copyProperties(account, accountBalanceDto);
            return accountBalanceDto;
        }).collect(Collectors.toList());
    }

    public void createAccount(Account account) {
        accountRepository.save(account);
    }

    public void updateAccount(Account account) {
        accountRepository.save(account);
    }

    public Account validateAccount(String accountId, Double amount, TransactionType transactionType) throws Exception {
        Account account = accountRepository.findByAccountId(accountId);

        // check if account exists
        if (account == null) {
            throw new Exception("Account not found");
        }

        switch (transactionType){
            case CREDIT:
                if (account.getAccountBalance() + amount > account.getCreditLimit()) {
                    throw new Exception("Credit limit exceeded");
                }
                break;
            case DEBIT:
                if (account.getAccountBalance() - amount < (-1*account.getOverdraftLimit())) {
                    throw new Exception("Overdraft limit exceeded");
                }
                break;
        }

        return account;
    }
}
