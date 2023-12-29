package com.example.mfb_ussd_process_flow.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceResponse {
    @JsonProperty("Status")
    private String Status;

    @JsonProperty("Response")
    private Response Response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        @JsonProperty("Retval")
        private int Retval;

        @JsonProperty("Retmsg")
        private String Retmsg;

        @JsonProperty("AvailableBalance")
        private BigDecimal AvailableBalance;

        @JsonProperty("UsableBalance")
        private BigDecimal UsableBalance;

        @JsonProperty("AcctName")
        private String AcctName;

        @JsonProperty("accountNumber")
        private String accountNumber;
    }
}
