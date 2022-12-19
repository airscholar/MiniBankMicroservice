package com.airscholar.AccountService.command.api.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedEvent {
    private String accountId;
    private String accountName;
    private Double accountBalance;
    private Double creditLimit;
    private long overdraftLimit;
}
