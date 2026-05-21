package com.example.account_service.kafka;

import com.example.account_service.dto.TransactionEvent;
import com.example.account_service.enity.Account;
import com.example.account_service.enums.EventType;
import com.example.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AccountRepository repository;
    private final KafkaProducerService producer;

    @KafkaListener(
            topics = "transaction-topic",
            groupId = "account-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(TransactionEvent event) {

        try {

            System.out.println("\n========================================");
            System.out.println("ACCOUNT SERVICE RECEIVED EVENT");
            System.out.println("EVENT : " + event);
            System.out.println("========================================\n");

            if (event == null) {
                return;
            }

            if (event.getEventType() != EventType.TRANSFER_INITIATED) {
                return;
            }

            // FETCH FROM ACCOUNT
            Account fromAccount = repository
                    .findByAccountNumber(String.valueOf(event.getFromAccount()))
                    .orElseThrow(() ->
                            new RuntimeException("From account not found"));

            // FETCH TO ACCOUNT
            Account toAccount = repository
                    .findByAccountNumber(String.valueOf(event.getToAccount()))
                    .orElseThrow(() ->
                            new RuntimeException("To account not found"));

            System.out.println("FROM ACCOUNT FOUND : "
                    + fromAccount.getAccountNumber());

            System.out.println("TO ACCOUNT FOUND : "
                    + toAccount.getAccountNumber());

            System.out.println("\nCURRENT BALANCES");
            System.out.println("FROM ACCOUNT BALANCE : "
                    + fromAccount.getBalance());

            System.out.println("TO ACCOUNT BALANCE : "
                    + toAccount.getBalance());

            // CHECK BALANCE
            if (fromAccount.getBalance() < event.getAmount()) {

                System.out.println("\nINSUFFICIENT BALANCE");
                System.out.println("TRANSFER FAILED");

                sendFailure(event);
                return;
            }

            // ================= DEBIT =================

            System.out.println("\n========== STARTING DEBIT ==========");

            Double oldFromBalance = fromAccount.getBalance();

            Double newFromBalance =
                    oldFromBalance - event.getAmount();

            System.out.println("OLD FROM ACCOUNT BALANCE : "
                    + oldFromBalance);

            System.out.println("TRANSFER AMOUNT : "
                    + event.getAmount());

            System.out.println("NEW FROM ACCOUNT BALANCE : "
                    + newFromBalance);

            fromAccount.setBalance(newFromBalance);

            repository.save(fromAccount);

            System.out.println("DEBIT UPDATED IN DATABASE SUCCESSFULLY");

            producer.sendEvent(
                    new TransactionEvent(
                            EventType.AMOUNT_DEBITED,
                            event.getTransactionId(),
                            event.getFromAccount(),
                            event.getToAccount(),
                            event.getAmount()
                    )
            );

            System.out.println("DEBIT EVENT SENT TO KAFKA");

            // ================= CREDIT =================

            System.out.println("\n========== STARTING CREDIT ==========");

            Double oldToBalance = toAccount.getBalance();

            Double newToBalance =
                    oldToBalance + event.getAmount();

            System.out.println("OLD TO ACCOUNT BALANCE : "
                    + oldToBalance);

            System.out.println("TRANSFER AMOUNT : "
                    + event.getAmount());

            System.out.println("NEW TO ACCOUNT BALANCE : "
                    + newToBalance);

            toAccount.setBalance(newToBalance);

            repository.save(toAccount);

            System.out.println("CREDIT UPDATED IN DATABASE SUCCESSFULLY");

            producer.sendEvent(
                    new TransactionEvent(
                            EventType.AMOUNT_CREDITED,
                            event.getTransactionId(),
                            event.getFromAccount(),
                            event.getToAccount(),
                            event.getAmount()
                    )
            );

            System.out.println("CREDIT EVENT SENT TO KAFKA");

            // ================= SUCCESS =================

            System.out.println("\n========================================");
            System.out.println("TRANSFER COMPLETED SUCCESSFULLY");
            System.out.println("TRANSACTION ID : "
                    + event.getTransactionId());
            System.out.println("========================================\n");

        } catch (Exception e) {

            System.out.println("\n========================================");
            System.out.println("ACCOUNT SERVICE ERROR");
            System.out.println("ERROR MESSAGE : " + e.getMessage());
            System.out.println("========================================\n");
        }
    }

    private void sendFailure(TransactionEvent event) {

        System.out.println("\nSENDING TRANSFER FAILED EVENT");

        producer.sendEvent(
                new TransactionEvent(
                        EventType.TRANSFER_FAILED,
                        event.getTransactionId(),
                        event.getFromAccount(),
                        event.getToAccount(),
                        event.getAmount()
                )
        );
    }
}