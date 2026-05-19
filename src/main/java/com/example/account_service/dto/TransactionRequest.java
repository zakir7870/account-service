package com.example.account_service.dto;


import lombok.Data;

@Data
public class TransactionRequest {

    private String fromAccount;
    private String toAccount;

}
