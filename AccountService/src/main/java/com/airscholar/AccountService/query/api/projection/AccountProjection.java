package com.airscholar.AccountService.query.api.projection;

import com.airscholar.AccountService.command.api.dto.AccountBalanceDto;
import com.airscholar.AccountService.command.api.dto.AccountDto;
import com.airscholar.AccountService.command.api.service.AccountService;
import com.airscholar.AccountService.data.Account;
import com.airscholar.BankService.command.api.data.JournalEntry;
import com.airscholar.CommonService.queries.account.GetAccountBalanceQuery;
import com.airscholar.AccountService.query.api.queries.GetAccountByAccountIdQuery;
import com.airscholar.CommonService.queries.account.GetAllAccountsQuery;
import com.airscholar.CommonService.queries.account.GetAllRedAccountsQuery;
import com.airscholar.CommonService.queries.bank.GetTransactionsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AccountProjection {

    private AccountService accountService;
    private QueryGateway queryGateway;

    public AccountProjection(AccountService accountService, QueryGateway queryGateway) {
        this.accountService = accountService;
        this.queryGateway = queryGateway;
    }

    @QueryHandler
    public List<AccountDto> handle(GetAllAccountsQuery query) {
        log.info("Handling query to get all accounts=> {}", query);

        List<AccountDto> accounts = accountService.getAllAccounts().stream().map(
                account -> AccountDto
                        .builder()
                        .accountId(account.getAccountId())
                        .accountBalance(account.getAccountBalance())
                        .accountName(account.getAccountName())
                        .creditLimit(account.getCreditLimit())
                        .overdraftLimit(account.getOverdraftLimit())
                        .build()).collect(Collectors.toList());
        return accounts;
    }

    @QueryHandler
    public AccountDto handle(GetAccountByAccountIdQuery query){
        log.info("Handling query to get account by account Id => {}", query);

        Account account = accountService.getAccountByAccountId(query.getAccountId());

        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(account.getAccountId());
        accountDto.setAccountBalance(account.getAccountBalance());
        accountDto.setAccountName(account.getAccountName());
        accountDto.setCreditLimit(account.getCreditLimit());
        accountDto.setOverdraftLimit(account.getOverdraftLimit());

        return accountDto;
    }

    @QueryHandler
    public List<AccountBalanceDto> handle(GetAllRedAccountsQuery query){
        log.info("Handling query to get all red accounts => {}", query);

        List<AccountBalanceDto> accounts = accountService.getAllRedAccounts().stream().map(
                account -> AccountBalanceDto
                        .builder()
                        .accountId(account.getAccountId())
                        .accountBalance(account.getAccountBalance())
                        .accountName(account.getAccountName())
                        .build()).collect(Collectors.toList());
        return accounts;
    }
}
