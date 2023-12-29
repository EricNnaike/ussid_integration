package com.example.mfb_ussd_process_flow.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RetrieveAccountRequest {
    private String phoneNumber;
}
