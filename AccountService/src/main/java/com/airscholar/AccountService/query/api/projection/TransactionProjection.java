package com.airscholar.AccountService.query.api.projection;

import com.airscholar.AccountService.command.api.dto.AccountBalanceDto;
import com.airscholar.AccountService.command.api.dto.AccountDto;
import com.airscholar.AccountService.command.api.service.AccountService;
import com.airscholar.AccountService.data.Account;
import com.airscholar.BankService.command.api.data.JournalEntry;
import com.airscholar.CommonService.queries.account.GetAccountBalanceQuery;
import com.airscholar.CommonService.queries.bank.GetTransactionsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TransactionProjection {
    private AccountService accountService;
    private QueryGateway queryGateway;

    public TransactionProjection(AccountService accountService, QueryGateway queryGateway) {
        this.accountService = accountService;
        this.queryGateway = queryGateway;
    }

    @QueryHandler
    public AccountBalanceDto handle(GetAccountBalanceQuery query){
        log.info("Handling query to get account balance for {}", query.getAccountId());
        Account account = accountService.getAccountBalanceByAccountId(query.getAccountId());
        AccountBalanceDto accountBalanceDto = new AccountBalanceDto();

        accountBalanceDto.setAccountId(account.getAccountId());
        accountBalanceDto.setAccountBalance(account.getAccountBalance());
        accountBalanceDto.setAccountName(account.getAccountName());

        return accountBalanceDto;
    }
}
