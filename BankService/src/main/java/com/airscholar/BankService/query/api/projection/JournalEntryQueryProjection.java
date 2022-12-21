package com.airscholar.BankService.query.api.projection;

import com.airscholar.BankService.command.api.data.JournalEntry;
import com.airscholar.BankService.command.api.service.BankService;
import com.airscholar.CommonService.queries.bank.GetTransactionsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JournalEntryQueryProjection {
    private QueryGateway queryGateway;
    private BankService bankService;

    public JournalEntryQueryProjection(QueryGateway queryGateway, BankService bankService) {
        this.queryGateway = queryGateway;
        this.bankService = bankService;
    }

    @QueryHandler
    public List<JournalEntry> handle(GetTransactionsQuery query){
        log.info("Handling query to get transactions for {}", query);
        List<JournalEntry> journalEntries = bankService.getTransactionsByAccountId(query.getAccountId()).stream().filter(
                journalEntry -> {
                    if(Objects.equals(journalEntry.getAccountId(), query.getAccountId())){
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());

        Collections.sort(journalEntries, Collections.reverseOrder());

        return journalEntries;
    }
}
