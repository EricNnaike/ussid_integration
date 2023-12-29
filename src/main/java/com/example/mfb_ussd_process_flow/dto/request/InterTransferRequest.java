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
public class InterTransferRequest {
    private String debitAcct;
    private String beneficiaryAccount;
    private String sourceAccount;
    private BigDecimal amount;
    private String beneficiaryBank;
    private String narration;
    private String transRef;
    private String checkSum;

}
