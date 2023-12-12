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
public class OpenAccountResponse {
    @JsonProperty("Status")
    private String status;

    @JsonProperty("Response")
    private Response response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Response {
        @JsonProperty("Retval")
        private int retval;

        @JsonProperty("Retmsg")
        private String retmsg;

        @JsonProperty("AccountNumber")
        private String accountNumber;

        @JsonProperty("CustomerID")
        private String customerID;

        @JsonProperty("Fullname")
        private String fullname;
    }

}
