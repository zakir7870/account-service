package com.example.account_service.service;

import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.enity.Account;

import java.util.List;

public interface AccountService {

    AccountResponse create(AccountRequest request);
    
    Account getAccount(Long id);

    Double getBalance(Long id);

    List<Account> getAllAccounts();

    Account getByAccountNumber(String accountNumber);

    Account updateAccount(AccountRequest request);


    String transfer(TransactionRequest request);


}
