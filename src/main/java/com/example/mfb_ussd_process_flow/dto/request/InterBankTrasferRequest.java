package com.example.mfb_ussd_process_flow.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterBankTrasferRequest {
    private String phoneNumber;
    private String beneficiaryAccount;
    private BigDecimal amount;
    private String beneficiaryBank;
    private String narration;
}
