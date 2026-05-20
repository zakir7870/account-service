package com.example.account_service.kafka;

import com.example.account_service.dto.TransactionEvent;
import com.example.account_service.enity.Account;
import com.example.account_service.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AccountRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "transaction-topic", groupId = "account-group")
    @Transactional
    public void consume(String message) {

        try {
            System.out.println("RAW MESSAGE: " + message);

            // Convert JSON string → Object manually
            TransactionEvent event =
                    objectMapper.readValue(message, TransactionEvent.class);

            System.out.println("PARSED EVENT: " + event);

            // 1. Fetch accounts
            Account fromAccount = repository.findByAccountNumber(
                    String.valueOf(event.getFromAccount())
            ).orElseThrow(() -> new RuntimeException("From account not found"));

            Account toAccount = repository.findByAccountNumber(
                    String.valueOf(event.getToAccount())
            ).orElseThrow(() -> new RuntimeException("To account not found"));

            // 2. Validate balance
            if (fromAccount.getBalance() == null ||
                    fromAccount.getBalance() < event.getAmount()) {

                throw new RuntimeException("Insufficient balance");
            }

            // 3. Debit
            fromAccount.setBalance(
                    fromAccount.getBalance() - event.getAmount()
            );

            // 4. Credit
            toAccount.setBalance(
                    toAccount.getBalance() + event.getAmount()
            );

            // 5. Save both
            repository.save(fromAccount);
            repository.save(toAccount);

            System.out.println("Transaction processed successfully");

        } catch (Exception e) {
            System.err.println(" Kafka Consumer Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}