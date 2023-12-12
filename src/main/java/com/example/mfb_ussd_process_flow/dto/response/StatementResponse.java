package com.example.mfb_ussd_process_flow.dto.response;

import com.example.mfb_ussd_process_flow.utils.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatementResponse {
    @JsonProperty("Status")
    private String Status;

    @JsonProperty("Response")
    private List<ResponseData> Response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseData {
        @JsonProperty("Retval")
        private int Retval;

        @JsonProperty("Retmsg")
        private String Retmsg;

        @JsonProperty("AccountNumber")
        private String AccountNumber;

        @JsonProperty("Product")
        private String Product;

        @JsonProperty("TranDate")
        private String TranDate;

        @JsonProperty("ValueDate")
        private String ValueDate;

        @JsonProperty("Narration")
        private String Narration;

        @JsonProperty("Debit")
        private BigDecimal Debit;

        @JsonProperty("Credit")
        private BigDecimal Credit;

        @JsonProperty("BkBalance")
        private BigDecimal BkBalance;

        @JsonProperty("Postseq")
        private String Postseq;

        @JsonProperty("TellerNo")
        private String TellerNo;

        @JsonProperty("FullName")
        private String FullName;

        @JsonProperty("OpenBalance")
        private BigDecimal OpenBalance;

        @JsonProperty("CloseBalance")
        private BigDecimal CloseBalance;

        @JsonProperty("BranchName")
        private String BranchName;

        @JsonProperty("BranchAddress")
        private String BranchAddress;

        @JsonProperty("StartDate")
//        @JsonDeserialize(using = CustomDateDeserializer.class)
        private String StartDate;

        @JsonProperty("EndDate")
//        @JsonDeserialize(using = CustomDateDeserializer.class)
        private String EndDate;
    }

}
