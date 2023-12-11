package com.example.mfb_ussd_process_flow.utils;

import lombok.Data;

import java.math.BigDecimal;

public class Constants {
    public static final String bvnVerificationURL = "http://localhost:8080/api/v1/bvn-verification/";
    public static final String openAccountURL = "http://localhost:8080/api/v1/create-account";
    public static final String accountBalanceURL = "http://localhost:8080/api/v1/balance?accountNumber=";
    public static final String accountDetailsURL = "http://localhost:9001/api/v1/account/account-details";
    public static final String servicePaymentUrl = "http://localhost:8080/api/v1/account-GI";
    public static final String intraBankTransferUrl = "http://localhost:8080/api/v1/intra-transfer";
    public static final String intraBankNameEnquiryUrl = "http://localhost:8080/api/v1/name-enquiry?accountNum=";
    public static final String interBankNameEnquiryUrl = "http://localhost:8080/api/v1/interbank-nameEnquiry";
    public static final String interBankTransferUrl = "http://localhost:8080/api/v1/inter-transfer";
    public static final String accountStatementUrl = "http://localhost:8080/api/v1/statement?";
    public static final BigDecimal serviceFee = BigDecimal.valueOf(10);

}
