package com.example.mfb_ussd_process_flow.dto.request;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class IntraBankTransferRequest {
    private String debitAcct;
    private String creditAcct;
    private BigDecimal payamount;
    private String narration;
    private String requestID;
    private String transactionHash;
}
