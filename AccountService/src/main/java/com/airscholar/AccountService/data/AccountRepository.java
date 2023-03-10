package com.airscholar.AccountService.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    public Account findByAccountId(String accountId);
}
