package com.airscholar.AccountService.command.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Builder
public class AccountDto {
    private String accountName;
    private Double accountBalance;
    private Double creditLimit;
    private long overdraftLimit;
}