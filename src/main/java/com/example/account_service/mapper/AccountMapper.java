package com.example.account_service.mapper;


import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.enity.Account;

public class AccountMapper {

    public static Account toEntity(AccountRequest request) {
        Account account = new Account();
        account.setCustomerName(request.getCustomerName());
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getBalance());
        return account;
    }

    public static AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setName(account.getCustomerName());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        return response;
    }
}
