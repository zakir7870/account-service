package com.example.account_service.controller;

import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.enity.Account;
import com.example.account_service.mapper.AccountMapper;
import com.example.account_service.repository.AccountRepository;
import com.example.account_service.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountRepository repository;
    @Autowired
    private AccountService service;

    // Create

    @PostMapping("/create")
    public AccountResponse create(@Valid @RequestBody AccountRequest request,
                                  HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.create(request, username);
    }


    // Get by AccountNumber

    @PostMapping("/get")
    public Account get(@RequestBody AccountRequest request,
                       HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.getByAccountNumber(request.getAccountNumber(), username);
    }


    // Update

    @PutMapping("/update")
    public Account update(@RequestBody AccountRequest request,
                          HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.updateAccount(request, username);
    }


    // Get Balance

    @PostMapping("/balance")
    public Double getBalance(@RequestBody AccountRequest request,
                             HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.getBalance(Long.valueOf(request.getAccountNumber()), username);
    }


    //  NEW API → Kafka

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransactionRequest request,
                           HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.transfer(request, username);
    }
}