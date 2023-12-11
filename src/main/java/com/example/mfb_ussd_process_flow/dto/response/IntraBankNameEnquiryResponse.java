package com.example.mfb_ussd_process_flow.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntraBankNameEnquiryResponse {
    @JsonProperty("Status")
    private String Status;

    @JsonProperty("Response")
    private Response Response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        @JsonProperty("Retval")
        private int Retval;
        @JsonProperty("Retmsg")
        private String Retmsg;
        @JsonProperty("AccountNo")
        private String AccountNo;
        @JsonProperty("AcctName")
        private String AcctName;
    }

}
