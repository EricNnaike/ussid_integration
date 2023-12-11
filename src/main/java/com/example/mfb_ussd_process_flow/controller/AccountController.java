package com.example.mfb_ussd_process_flow.controller;

import com.example.mfb_ussd_process_flow.dto.request.*;
import com.example.mfb_ussd_process_flow.dto.response.*;
import com.example.mfb_ussd_process_flow.exceptions.MFBException;
import com.example.mfb_ussd_process_flow.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@AllArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/open")
    @ResponseStatus(HttpStatus.CREATED)
    public OpenAccountResponse openAccount(@RequestBody BVNVerificationRequest bvnVerificationRequest)
            throws Exception {
        return accountService.openAccount(bvnVerificationRequest);
    }

    @PostMapping("/balance")
    @ResponseStatus(HttpStatus.OK)
    public AccountBalanceResponse checkBalance(@RequestBody CheckBalanceRequest checkBalanceRequest)
            throws Throwable {
        return accountService.checkBalance(checkBalanceRequest);
    }

    @PostMapping("/intraBank-transfer")
    @ResponseStatus(HttpStatus.OK)
    public IntraBankTransferResponse intraBankTransfer(@RequestBody TransferRequest transferRequest) throws Exception {
        return accountService.intraBankTransfer(transferRequest);
    }

    @PostMapping("/interBank-transfer")
    @ResponseStatus(HttpStatus.OK)
    public InterBankTransferResponse interBankTransfer(@RequestBody InterBankTrasferRequest interBankTrasferRequest) throws Exception {
        return accountService.interBankTransfer(interBankTrasferRequest);
    }

    @PostMapping("/account-statement")
    @ResponseStatus(HttpStatus.OK)
    public StatementResponse miniStatement(@RequestBody AccountRequest accountRequest) throws Throwable {
        return accountService.miniStatement(accountRequest);
    }


}
