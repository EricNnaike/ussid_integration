package com.example.mfb_ussd_process_flow.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NameEnquiryRequest {
    private String bankCode;
    private String accountNumber;
}
