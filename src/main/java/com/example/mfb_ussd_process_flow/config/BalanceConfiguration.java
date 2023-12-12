package com.example.mfb_ussd_process_flow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BalanceConfiguration {
    @Value("${check.balance.enabled}")
    private boolean isCheckBalanceEnabled;

    public boolean isCheckBalanceEnabled() {
        return isCheckBalanceEnabled;
    }
}
