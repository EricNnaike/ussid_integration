package com.example.mfb_ussd_process_flow.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    private String phoneNumber;
    private String startDate;
    private String endDate;
    private String creditAcct;
    private BigDecimal payamount;
    private String narration;

}
