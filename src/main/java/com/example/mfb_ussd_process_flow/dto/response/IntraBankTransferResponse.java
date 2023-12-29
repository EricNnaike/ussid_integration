package com.example.mfb_ussd_process_flow.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IntraBankTransferResponse {
     @JsonProperty("Status")
     private String status;

     @JsonProperty("Response")
     private IntraTransfer response;

     @Getter
     @Setter
     @ToString
     @AllArgsConstructor
     @NoArgsConstructor
     public static class IntraTransfer {

          @JsonProperty("RetVal")
          private int retVal;

          @JsonProperty("RetMsg")
          private String retMsg;

          @JsonProperty("PostingSequence")
          private String postingSequence;

          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
          private String postdate;

          @JsonProperty("RequestID")
          private String requestId;

          @JsonProperty("TranAmt")
          private int tranAmt;
     }

}
