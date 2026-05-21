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

    @KafkaListener(topics = "transaction-topic", groupId = "account-group")
    @Transactional
    public void consume(TransactionEvent event) {

        try {
            System.out.println("RECEIVED EVENT: " + event);

            if (event.getEventType() != EventType.TRANSFER_INITIATED) {
                return;
            }

            // Fetch accounts
            Account fromAccount = repository
                    .findByAccountNumber(String.valueOf(event.getFromAccount()))
                    .orElseThrow(() -> new RuntimeException("From account not found"));

            Account toAccount = repository
                    .findByAccountNumber(String.valueOf(event.getToAccount()))
                    .orElseThrow(() -> new RuntimeException("To account not found"));

            // Check balance
            if (fromAccount.getBalance() == null ||
                    fromAccount.getBalance() < event.getAmount()) {

                sendFailure(event);
                return;
            }

            //  STEP 1: DEBIT
            fromAccount.setBalance(
                    fromAccount.getBalance() - event.getAmount()
            );
            repository.save(fromAccount);

            producer.sendEvent(new TransactionEvent(
                    EventType.AMOUNT_DEBITED,
                    event.getTransactionId(),
                    event.getFromAccount(),
                    event.getToAccount(),
                    event.getAmount()
            ));

            //  STEP 2: CREDIT (WITH FAILURE HANDLING)
            try {

                toAccount.setBalance(
                        toAccount.getBalance() + event.getAmount()
                );
                repository.save(toAccount);

                producer.sendEvent(new TransactionEvent(
                        EventType.AMOUNT_CREDITED,
                        event.getTransactionId(),
                        event.getFromAccount(),
                        event.getToAccount(),
                        event.getAmount()
                ));

                System.out.println(" Transfer completed successfully");

            } catch (Exception creditException) {

                System.err.println(" Credit failed, starting compensation");

                //  COMPENSATION (ROLLBACK DEBIT)
                fromAccount.setBalance(
                        fromAccount.getBalance() + event.getAmount()
                );
                repository.save(fromAccount);

                producer.sendEvent(new TransactionEvent(
                        EventType.TRANSFER_COMPENSATED,
                        event.getTransactionId(),
                        event.getFromAccount(),
                        event.getToAccount(),
                        event.getAmount()
                ));
            }

        } catch (Exception e) {

            System.err.println(" Kafka Consumer Error: " + e.getMessage());
            sendFailure(eventSafe(event));
        }
    }

    //  FAILURE EVENT
    private void sendFailure(TransactionEvent event) {
        producer.sendEvent(new TransactionEvent(
                EventType.TRANSFER_FAILED,
                event.getTransactionId(),
                event.getFromAccount(),
                event.getToAccount(),
                event.getAmount()
        ));
    }

    //  Safe fallback
    private TransactionEvent eventSafe(TransactionEvent event) {
        return event == null ? new TransactionEvent() : event;
    }
}