package com.example.account_service.kafka;

import com.example.account_service.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void sendEvent(TransactionEvent event) {
        kafkaTemplate.send("account-topic", event);
    }
}
