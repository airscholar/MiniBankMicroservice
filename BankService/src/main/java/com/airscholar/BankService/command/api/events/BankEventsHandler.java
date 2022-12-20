package com.airscholar.BankService.command.api.events;

import com.airscholar.BankService.command.api.data.JournalEntry;
import com.airscholar.BankService.command.api.data.JournalEntryRepository;
import com.airscholar.CommonService.command.DepositMoneyCommand;
import com.airscholar.CommonService.command.WithdrawMoneyCommand;
import com.airscholar.CommonService.events.CompleteDepositEvent;
import com.airscholar.CommonService.events.DepositMoneyCreatedEvent;
import com.airscholar.CommonService.events.WithdrawMoneyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BankEventsHandler {
    private final JournalEntryRepository journalEntryRepository;

    public BankEventsHandler(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    @EventHandler
    public void on(DepositMoneyCreatedEvent event){
        log.info("DepositMoneyCreatedEvent: {}", event);
        JournalEntry journalEntry = JournalEntry.builder()
                .transactionStatus(event.getTransactionStatus())
                .transactionId(event.getTransactionId())
                .transactionType(event.getTransactionType())
                .transactionAmount(event.getAmount())
                .transactionDate(event.getTransactionDate())
                .accountId(event.getAccountId())
                .build();

        log.info("Saving event in the DB: {}", event);
        journalEntryRepository.save(journalEntry);
    }

    @EventHandler
    public void on(WithdrawMoneyCreatedEvent event) {
        log.info("WithdrawMoneyCommand: {}", event);
    }

    @EventHandler
    public void on(CompleteDepositEvent event) throws Exception {
        log.info("CompleteDepositEvent: {}", event);
        JournalEntry journalEntry = journalEntryRepository.findByTransactionId(event.getTransactionId());

        if(journalEntry == null) {
            //start the compensation transaction
            throw new Exception("No journal entry found for transaction id: " + event.getTransactionId());
        }

        journalEntry.setTransactionStatus(event.getTransactionStatus());

        journalEntryRepository.save(journalEntry);
    }
}
