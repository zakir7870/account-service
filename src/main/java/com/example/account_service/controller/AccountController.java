package com.example.account_service.controller;

import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.enity.Account;
import com.example.account_service.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService service;



    // CREATE ACCOUNT

    @PostMapping("/create")
    public AccountResponse create(@Valid @RequestBody AccountRequest request,
                                  HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.create(request, username);
    }

    // GET ACCOUNT BY ACCOUNT NUMBER

    @PostMapping("/get")
    public Account get(@RequestBody AccountRequest request,
                       HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.getByAccountNumber(
                request.getAccountNumber(),
                username
        );
    }

    // UPDATE ACCOUNT

    @PutMapping("/update")
    public Account update(@RequestBody AccountRequest request,
                          HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.updateAccount(request, username);
    }

    // GET BALANCE

    @PostMapping("/balance")
    public Double getBalance(@RequestBody AccountRequest request,
                             HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.getBalance(
                request.getAccountNumber(),
                username
        );
    }

    // TRANSFER

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransactionRequest request,
                           HttpServletRequest httpRequest) {

        String username = httpRequest.getHeader("X-User");

        return service.transfer(request, username);
    }

//    @GetMapping("/all")
//    public List<Account> getAllAccounts(HttpServletRequest httpRequest) {
//
//        String username = httpRequest.getHeader("X-User");
//
//        return service.getAllAccounts(username);
//    }

    @GetMapping("/all")
    public Page<Account> getAllAccounts(
            HttpServletRequest httpRequest,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "5") int size
    ) {

        String username = httpRequest.getHeader("X-User");

        return service.getAllAccounts(
                username,
                page,
                size
        );
    }
}