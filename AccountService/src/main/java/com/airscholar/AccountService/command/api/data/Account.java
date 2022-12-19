package com.airscholar.AccountService.command.api.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "accounts")
public class Account {
    @Id
    private String accountId;
    private String accountName;
    private Double accountBalance;
    private Double creditLimit;
    private long overdraftLimit;
}
