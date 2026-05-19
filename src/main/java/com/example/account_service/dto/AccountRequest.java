package com.example.account_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AccountRequest {

    @NotBlank
    private String customerName;

    @NotBlank
    private String accountNumber;

    @NotNull
    private Double balance;
}