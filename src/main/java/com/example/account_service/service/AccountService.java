package com.example.account_service.service;

import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.enity.Account;

import java.util.List;

public interface AccountService {

    AccountResponse create(AccountRequest request,String userName);
    
    Account getAccount(Long id,String userName);

    Double getBalance(Long id,String userName);

    List<Account> getAllAccounts();

    Account getByAccountNumber(String accountNumber,String userName);

    Account updateAccount(AccountRequest request, String userName);


    String transfer(TransactionRequest request, String userName);


}
