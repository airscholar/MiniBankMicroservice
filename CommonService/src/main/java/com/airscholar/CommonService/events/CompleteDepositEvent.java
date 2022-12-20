package com.airscholar.CommonService.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteDepositEvent {
    private String transactionId;
    private String transactionStatus;
}
