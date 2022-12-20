package com.airscholar.CommonService.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawMoneyCommand {
    @TargetAggregateIdentifier
    private String transactionId;
    private String accountId;
    private Double amount;
    private String transactionStatus;

}
