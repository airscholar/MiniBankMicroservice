package com.airscholar.AccountService.query.api.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GetAccountByAccountIdQuery {
    private String accountId;
}
