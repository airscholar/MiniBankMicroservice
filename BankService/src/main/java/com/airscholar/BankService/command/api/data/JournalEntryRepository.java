package com.airscholar.BankService.command.api.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, String> {

    public JournalEntry findByTransactionId(String transactionId);
}
