package com.airscholar.BankService.command.api.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositMoneyCreatedEvent {
    private String transactionId;
    private String accountId;
    private Double amount;
    private String transactionDate;
    private String transactionType;
    private String transactionStatus;
}
