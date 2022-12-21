package com.airscholar.AccountService.query.api.controller;

import com.airscholar.AccountService.command.api.dto.AccountBalanceDto;
import com.airscholar.AccountService.command.api.dto.AccountDto;
import com.airscholar.CommonService.queries.account.GetAccountBalanceQuery;
import com.airscholar.AccountService.query.api.queries.GetAccountByAccountIdQuery;
import com.airscholar.CommonService.queries.account.GetAllAccountsQuery;
import com.airscholar.CommonService.queries.account.GetAllRedAccountsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountQueryController {

    private QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<AccountDto> getAllAccounts() {
        GetAllAccountsQuery getAccountsQuery = new GetAllAccountsQuery();

        return queryGateway.query(getAccountsQuery, ResponseTypes.multipleInstancesOf(AccountDto.class)).join();
    }

    @GetMapping("/{accountId}")
    public AccountDto getAccountById(@PathVariable String accountId) {
        GetAccountByAccountIdQuery getAccountByAccountIdQuery = new GetAccountByAccountIdQuery(accountId);

        return queryGateway.query(getAccountByAccountIdQuery, ResponseTypes.instanceOf(AccountDto.class)).join();
    }

    @GetMapping("/{accountId}/balance")
    public AccountBalanceDto getAccountBalance(@PathVariable String accountId) {
        GetAccountBalanceQuery getAccountBalanceQuery = new GetAccountBalanceQuery(accountId);

        return queryGateway.query(getAccountBalanceQuery, ResponseTypes.instanceOf(AccountBalanceDto.class)).join();
    }

    @GetMapping("/red")
    public List<AccountBalanceDto> getAllRedAccounts(){
        GetAllRedAccountsQuery getAllRedAccountsQuery = new GetAllRedAccountsQuery();

        return queryGateway.query(getAllRedAccountsQuery, ResponseTypes.multipleInstancesOf(AccountBalanceDto.class)).join();
    }
}
