package com.airscholar.BankService.command.api.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelDepositEvent {
    private String transactionId;
    private String accountId;
    private String transactionType;
    private String transactionDate;
    private Double transactionAmount;
    private String transactionStatus;
}
