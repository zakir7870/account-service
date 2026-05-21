package com.example.account_service.kafka;

import com.example.account_service.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final String TOPIC = "transaction-topic";

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void sendEvent(TransactionEvent event) {

        System.out.println("ACCOUNT SERVICE SENDING EVENT : " + event);

        CompletableFuture<SendResult<String, TransactionEvent>> future =
                kafkaTemplate.send(TOPIC, event);

        future.whenComplete((result, ex) -> {

            if (ex == null) {

                System.out.println(
                        "EVENT SENT SUCCESSFULLY -> " +
                                result.getRecordMetadata().topic()
                );

            } else {

                System.err.println(
                        "FAILED TO SEND EVENT : " + ex.getMessage()
                );
            }
        });
    }
}