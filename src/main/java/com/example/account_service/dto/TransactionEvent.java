package com.example.account_service.dto;

import com.example.account_service.enums.EventType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEvent {

    private EventType eventType;

    private Long transactionId;

    private Long fromAccount;

    private Long toAccount;

    private Double amount;
}
