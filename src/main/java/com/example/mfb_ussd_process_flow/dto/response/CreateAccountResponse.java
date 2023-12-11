package com.example.mfb_ussd_process_flow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountResponse {
    private String accountNumber;
    private boolean success;
    private String responseMessage;
    private String message;
}
