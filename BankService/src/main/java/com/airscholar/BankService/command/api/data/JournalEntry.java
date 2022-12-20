package com.airscholar.BankService.command.api.data;

import com.airscholar.CommonService.enums.TransactionStatus;
import com.airscholar.CommonService.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "journal_entries")
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String accountId;
    private String transactionId;
    private String transactionType;
    private String transactionDate;
    private Double transactionAmount;
    private String transactionStatus;
}
