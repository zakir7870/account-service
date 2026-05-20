package com.example.account_service.dto;


import lombok.Data;

@Data
public class TransactionRequest {

    private Long transactionId;
    private Long fromAccount;
    private Long toAccount;
    private Double amount;

}
