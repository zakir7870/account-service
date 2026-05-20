package com.example.account_service.kafka;

import com.example.account_service.dto.TransactionEvent;
import com.example.account_service.enity.Account;
import com.example.account_service.repository.AccountRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final AccountRepository repository;

    public KafkaConsumer(AccountRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "transaction-topic", groupId = "account-group")
    public void consume(TransactionEvent event) {

        System.out.println(" Received Event: " + event);

        // Fetch accounts
        Account fromAccount = repository
                .findByAccountNumber(event.getFromAccount())
                .orElseThrow(() -> new RuntimeException("From account not found"));

        Account toAccount = repository
                .findByAccountNumber(event.getToAccount())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        //  Validate balance
        if (fromAccount.getBalance() < event.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        //  Debit
        fromAccount.setBalance(fromAccount.getBalance() - event.getAmount());

        //  Credit
        toAccount.setBalance(toAccount.getBalance() + event.getAmount());

        //  Save
        repository.save(fromAccount);
        repository.save(toAccount);

        System.out.println(" Transaction processed successfully");
    }
}