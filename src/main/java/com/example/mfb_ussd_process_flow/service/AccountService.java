package com.example.mfb_ussd_process_flow.service;

import com.example.mfb_ussd_process_flow.dto.request.*;
import com.example.mfb_ussd_process_flow.dto.response.*;
import com.example.mfb_ussd_process_flow.exceptions.MFBException;

public interface AccountService {
    OpenAccountResponse openAccount(BVNVerificationRequest bvnVerificationRequest) throws Exception;
    AccountBalanceResponse checkBalance(CheckBalanceRequest checkBalanceRequest) throws Throwable;
    IntraBankTransferResponse intraBankTransfer(TransferRequest transferRequest) throws Exception;
    InterBankTransferResponse interBankTransfer(InterBankTrasferRequest interBankTrasferRequest) throws Exception;
    StatementResponse miniStatement(AccountRequest accountRequest) throws Throwable;
}
