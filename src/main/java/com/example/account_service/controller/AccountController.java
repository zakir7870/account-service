package com.example.account_service.controller;

import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.enity.Account;
import com.example.account_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    // Create
    @PostMapping("/create")
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return service.create(request);
    }

    // Get by AccountNumber
    @PostMapping("/get")
    public Account get(@RequestBody AccountRequest request) {
        return service.getByAccountNumber(request.getAccountNumber());
    }

    // Update
    @PutMapping("/update")
    public Account update(@RequestBody AccountRequest request) {
        return service.updateAccount(request);
    }

    // Get Balance
    @PostMapping("/balance")
    public Double getBalance(@RequestBody AccountRequest request) {
        return service.getBalance(Long.valueOf(request.getAccountNumber()));
    }


    //  NEW API → Kafka
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransactionRequest request) {
        return service.transfer(request);
    }

}