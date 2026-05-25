package com.example.account_service.repository;

import com.example.account_service.enity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // FIND BY ACCOUNT NUMBER

    Optional<Account> findByAccountNumber(String accountNumber);

    // FIND BY ACCOUNT NUMBER + USERNAME

    Optional<Account> findByAccountNumberAndUsername(
            String accountNumber,
            String username
    );

    // GET ALL ACCOUNTS OF LOGGED-IN USER

    List<Account> findByUsername(String username);
}