package com.airscholar.CommonService.queries.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GetAccountBalanceQuery {
    private String accountId;
}
