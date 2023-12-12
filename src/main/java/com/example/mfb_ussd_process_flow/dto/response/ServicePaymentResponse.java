package com.example.mfb_ussd_process_flow.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString
public class ServicePaymentResponse {
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Response")
    private ResponseData response;

    @Data
    public static class ResponseData {
        @JsonProperty("Retval")
        private int retval;
        @JsonProperty("Retmsg")
        private String retmsg;
        @JsonProperty("PostingSequence")
        private String postingSequence;
        @JsonProperty("PostDate")
        private String PostDate;
        @JsonProperty("RequestID")
        private String requestID;
        @JsonProperty("TranAmt")
        private double tranAmt;
        @JsonProperty("DebitAcct")
        private String debitAcct;
        @JsonProperty("CreditAcct")
        private String creditAcct;
        @JsonProperty("Narration")
        private String narration;

    }
}
