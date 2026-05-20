package com.example.account_service.mapper;


import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.enity.Account;

public class AccountMapper {

    public static Account toEntity(AccountRequest request) {

        return Account.builder()
                .accountNumber(request.getAccountNumber())
                .customerName(request.getCustomerName())
                .balance(request.getBalance())
                .build();
    }

    public static AccountResponse toResponse(Account account) {

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .name(account.getCustomerName())
                .balance(account.getBalance())
                .build();
    }
}
