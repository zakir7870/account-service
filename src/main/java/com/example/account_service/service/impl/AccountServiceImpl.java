package com.example.account_service.service.impl;

import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.enity.Account;
import com.example.account_service.mapper.AccountMapper;
import com.example.account_service.repository.AccountRepository;
import com.example.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository repository;

    // CREATE ACCOUNT

    @Override
    @CacheEvict(value = {"accounts", "balance"}, allEntries = true)
    public AccountResponse create(AccountRequest request, String userName) {

        Account account = AccountMapper.toEntity(request);

        account.setUsername(userName);

        if (account.getAccountNumber() == null ||
                account.getAccountNumber().isEmpty()) {

            account.setAccountNumber(UUID.randomUUID().toString());
        }

        Account saved = repository.save(account);

        return AccountMapper.toResponse(saved);
    }

    // GET ACCOUNT

    @Override
    public Account getAccount(String accountNumber, String userName) {

        Account account = repository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateOwner(account, userName);

        return account;
    }

    // GET BALANCE

    @Override
    @Cacheable(value = "balance", key = "#accountNumber")
    public Double getBalance(String accountNumber, String userName) {

        return getAccount(accountNumber, userName).getBalance();
    }

//    // GET ALL ACCOUNTS
//
//    @Override
//    @Cacheable(value = "accounts", key = "#p0")
//    public List<Account> getAllAccounts(String userName) {
//
//        System.out.println("Fetching all accounts from DB...");
//
//        return repository.findByUsername(userName);
//    }

    // GET ALL ACCOUNTS WITH PAGINATION

    @Override
    public Page<Account> getAllAccounts(
            String userName,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        System.out.println("Fetching paginated accounts from DB...");

        return repository.findByUsername(
                userName,
                pageable
        );
    }

    // GET BY ACCOUNT NUMBER

    @Override
    @Cacheable(value = "accounts", key = "#accountNumber")
    public Account getByAccountNumber(String accountNumber, String userName) {

        System.out.println("Fetching account from DB...");

        Account account = repository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateOwner(account, userName);

        return account;
    }

    // UPDATE ACCOUNT

    @Override
    @CachePut(value = "accounts", key = "#request.accountNumber")
    public Account updateAccount(AccountRequest request, String userName) {

        Account account = repository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateOwner(account, userName);

        account.setCustomerName(request.getCustomerName());
        account.setBalance(request.getBalance());

        return repository.save(account);
    }

    // TRANSFER

    @Override
    public String transfer(TransactionRequest request, String userName) {

        return "Transfer handled by Transaction Service";
    }

    // SECURITY VALIDATION

    private void validateOwner(Account account, String userName) {

        if (account.getUsername() == null ||
                !account.getUsername().equals(userName)) {

            throw new RuntimeException("Unauthorized access");
        }
    }
}