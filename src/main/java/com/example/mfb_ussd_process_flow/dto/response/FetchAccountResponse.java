package com.example.mfb_ussd_process_flow.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchAccountResponse {
    private String accountnumber;
    private String customerid;
    private BigDecimal BKBalance;
}
