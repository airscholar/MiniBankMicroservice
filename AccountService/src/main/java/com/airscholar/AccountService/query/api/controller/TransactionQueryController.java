package com.airscholar.AccountService.query.api.controller;

import com.airscholar.BankService.command.api.data.JournalEntry;
import com.airscholar.CommonService.queries.bank.GetTransactionsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/transactions")
public class TransactionQueryController {
    private QueryGateway queryGateway;

    public TransactionQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/{accountId}")
    public List<JournalEntry> getTransactions(@PathVariable String accountId) {
        log.info("Getting transactions for account: {} between {} and {}", accountId);

        GetTransactionsQuery getTransactionsQuery = GetTransactionsQuery.builder()
                .accountId(accountId)

                .build();

        log.info("Query: {}", getTransactionsQuery);
        return queryGateway.query(getTransactionsQuery, ResponseTypes.multipleInstancesOf(JournalEntry.class)).join();
    }
}
