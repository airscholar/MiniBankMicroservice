package com.airscholar.AccountService.command.api.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateAccountCommand {
    @TargetAggregateIdentifier
    private String accountId;
    private String accountName;
    private Double accountBalance;
    private Double creditLimit;
    private long overdraftLimit;
}
