package com.example.account_service.service.impl;


import com.example.account_service.dto.AccountRequest;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.enity.Account;
import com.example.account_service.kafka.KafkaProducer;
import com.example.account_service.repository.AccountRepository;
import com.example.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private KafkaProducer producer;

    @Override
    public Account createAccount(Account account) {
        return repository.save(account);
    }

    @Override
    public Account getAccount(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public Double getBalance(Long id) {
        return getAccount(id).getBalance();
    }

    @Override
    public List<Account> getAllAccounts() {
        return repository.findAll();
    }


    @Override
    public Account getByAccountNumber(String accountNumber) {

        return repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }


    @Override
    public Account updateAccount(AccountRequest request) {

        Account account = repository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setCustomerName(request.getCustomerName());
        account.setBalance(request.getBalance());

        return repository.save(account);
    }


    // Kafka Producer trigger
    @Override
    public String transfer(TransactionRequest request) {

        producer.sendEvent(request);

        return "Transaction event sent to Kafka";
    }


}
