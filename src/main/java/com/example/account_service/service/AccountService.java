package com.example.account_service.service;

import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.enity.Account;

import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService {

    AccountResponse create(AccountRequest request, String userName);

    Account getAccount(String accountNumber, String userName);

    Double getBalance(String accountNumber, String userName);

//    List<Account> getAllAccounts(String userName);
    Page<Account> getAllAccounts(
        String userName,
        int page,
        int size
);
    Account getByAccountNumber(String accountNumber, String userName);

    Account updateAccount(AccountRequest request, String userName);

    String transfer(TransactionRequest request, String userName);
}