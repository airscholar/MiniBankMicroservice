package com.airscholar.BankService.command.api.service;

import com.airscholar.BankService.command.api.data.JournalEntry;
import com.airscholar.BankService.command.api.data.JournalEntryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankService {
    private JournalEntryRepository journalEntryRepository;

    public BankService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    public List<JournalEntry> getTransactionsByAccountId(String accountId){
        return journalEntryRepository.findByAccountId(accountId);
    }

}
