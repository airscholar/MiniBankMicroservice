package com.airscholar.BankService.command.api.events;

import com.airscholar.BankService.command.api.data.JournalEntry;
import com.airscholar.BankService.command.api.data.JournalEntryRepository;
import com.airscholar.CommonService.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Component
@Slf4j
public class BankEventsHandler {
    private final JournalEntryRepository journalEntryRepository;

    public BankEventsHandler(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    @EventHandler
    public void on(DepositMoneyCreatedEvent event) {
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
        JournalEntry journalEntry = JournalEntry.builder()
                .transactionStatus(event.getTransactionStatus())
                .transactionId(event.getTransactionId())
                .transactionType(event.getTransactionType())
                .transactionAmount(event.getAmount())
                .transactionDate(event.getTransactionDate())
                .accountId(event.getAccountId())
                .build();

        log.info("WithdrawMoneyCreatedEvent saved in DB: {}", event);
        journalEntryRepository.save(journalEntry);
    }

    @EventHandler
    public void on(CompleteDepositEvent event) throws Exception {
        log.info("CompleteDepositEvent: {}", event);
        JournalEntry journalEntry = journalEntryRepository.findByTransactionId(event.getTransactionId());

        journalEntry.setTransactionStatus(event.getTransactionStatus());

        journalEntryRepository.save(journalEntry);
    }

    @EventHandler
    public void on(CompleteWithdrawalEvent event) throws Exception {
        log.info("CompleteWithdrawalEvent: {}", event);
        JournalEntry journalEntry = journalEntryRepository.findByTransactionId(event.getTransactionId());

        journalEntry.setTransactionStatus(event.getTransactionStatus());

        journalEntryRepository.save(journalEntry);
    }

    @EventHandler
    public void on(CancelDepositEvent cancelDepositEvent) {
        log.info("CancelDepositEvent received from bank: {}", cancelDepositEvent);

        JournalEntry journalEntry = journalEntryRepository.findByTransactionId(cancelDepositEvent.getTransactionId());

        if (journalEntry == null) {
            throw new RuntimeException("No journal entry found for transaction id: " + cancelDepositEvent.getTransactionId());
        }

        journalEntry.setTransactionStatus(cancelDepositEvent.getTransactionStatus());

        journalEntryRepository.save(journalEntry);
    }

}