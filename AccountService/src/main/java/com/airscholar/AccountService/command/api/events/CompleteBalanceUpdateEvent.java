package com.airscholar.AccountService.command.api.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteBalanceUpdateEvent {
    private String transactionId;
    private String accountId;
    private Double amount;
    private String transactionStatus;
    private String transactionType;
}
