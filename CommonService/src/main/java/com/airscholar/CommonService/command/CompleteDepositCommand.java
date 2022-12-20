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
public class CompleteDepositCommand {
    @TargetAggregateIdentifier
    private String transactionId;
    private String transactionStatus;
}
