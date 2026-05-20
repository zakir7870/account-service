package com.example.account_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String name;
    private Double balance;
}
