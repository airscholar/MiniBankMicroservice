package com.airscholar.BankService.command.api.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, String> {

    public JournalEntry findByTransactionId(String transactionId);
    public List<JournalEntry> findByAccountId(String accountId);
}
