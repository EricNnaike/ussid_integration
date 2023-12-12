package com.example.mfb_ussd_process_flow.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InterBankNameEnquiryResponse {
    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("bankcode")
    private String bankCode;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("accountName")
    private String accountName;

    @JsonProperty("accountCurrency")
    private String accountCurrency;

    @JsonProperty("accountBvn")
    private String accountBvn;

    @JsonProperty("errorCode")
    private int errorCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("trustBancRef")
    private String trustBancRef;
}
