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
public class ServicePaymentRequest {
    private String debitAcct;
    private String creditAcct;
    private BigDecimal payamount;
    private String narration;
    private String requestID;
    private String transactionHash;

}
