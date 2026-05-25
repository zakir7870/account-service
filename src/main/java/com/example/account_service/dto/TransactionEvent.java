package com.example.account_service.dto;

import com.example.account_service.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEvent {
    private EventType eventType;
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
}
