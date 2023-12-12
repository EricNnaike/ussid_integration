package com.example.mfb_ussd_process_flow.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BVNVerificationRequest {
    private String bvn;
}
