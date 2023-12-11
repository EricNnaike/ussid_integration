package com.example.mfb_ussd_process_flow.service;

import com.example.mfb_ussd_process_flow.config.BalanceConfiguration;
import com.example.mfb_ussd_process_flow.dto.request.*;
import com.example.mfb_ussd_process_flow.dto.response.*;
import com.example.mfb_ussd_process_flow.entityAccount.TBLAccount;
import com.example.mfb_ussd_process_flow.entityUser.TransactionHistory;
import com.example.mfb_ussd_process_flow.entityUser.Users;
import com.example.mfb_ussd_process_flow.enums.PaymentStatus;
import com.example.mfb_ussd_process_flow.exceptions.MFBException;
import com.example.mfb_ussd_process_flow.repositoryAccount.CustomerRepository;
import com.example.mfb_ussd_process_flow.repositoryAccount.TBLAccountRepository;
import com.example.mfb_ussd_process_flow.repositoryUser.TransactionHistoryRepository;
import com.example.mfb_ussd_process_flow.repositoryUser.UserRepository;
import com.example.mfb_ussd_process_flow.utils.AppUtils;
import com.example.mfb_ussd_process_flow.utils.Constants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final WebClient.Builder webClient;
    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final TBLAccountRepository tblAccountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final BalanceConfiguration balanceConfiguration;
    private ExecutorService executor;
    @Value("${api.clientKey}")
    private String clientKey;

    @Value("${api.value}")
    private String clientKeyValue;

    @Value("${payload.BranchCode}")
    private String branchCode;

    @Value("${paload.ProductCode}")
    private String productCode;

    @Value("${payload.IDType}")
    private int IDType;

    @Value("${key.secretKey}")
    private String secretKey;

    @Value("${payload.AcctOfficer}")
    private String AcctOfficer;


    @Override
    public OpenAccountResponse openAccount(BVNVerificationRequest bvnVerificationRequest) throws Exception {
        log.info("incoming request {}",bvnVerificationRequest);

        String bvn = bvnVerificationRequest.getBvn();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username)
                .orElseThrow(() -> new MFBException("Invalid user"));

        OpenAccountRequest openAccountRequest = null;
        OpenAccountResponse openAccountResponse = null;
        //BVN VERIFICATION API
        try {
            ResponseEntity<BVNVerificationResponse> bvnVerificationResponse = performBVNVerification(bvn);
            if (bvnVerificationResponse.getStatusCode().is2xxSuccessful() && bvnVerificationResponse.getBody() != null) {
                BVNVerificationResponse result = bvnVerificationResponse.getBody();
                log.info("BVN info {}", result);
                openAccountRequest = getOpenAccountRequest(result);
                log.info("Open account request {}", openAccountRequest);
            }
        } catch (Exception ex) {
            throw new Exception("Error proccessing BVN", ex);
        }
        //OPEN ACCOUNT API
        try {
            ResponseEntity<OpenAccountResponse> response = handleOpenAccount(openAccountRequest);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                openAccountResponse = response.getBody();
                log.info("OpenAccountResponse......... {}", openAccountResponse);
            }
        } catch (Exception ex) {
            throw new Exception("Error processing open account request {}", ex);
        }
        return openAccountResponse;
    }

    private ResponseEntity<OpenAccountResponse> handleOpenAccount(OpenAccountRequest openAccountRequest) {
        String openAccountUrl = Constants.openAccountURL;
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<OpenAccountRequest> request = new HttpEntity<>(openAccountRequest, header);
        ResponseEntity<OpenAccountResponse> response = restTemplate.exchange(
                openAccountUrl,
                HttpMethod.POST,
                request,
                OpenAccountResponse.class
        );
        return response;
    }

    private ResponseEntity<BVNVerificationResponse> performBVNVerification(String bvn) {
        String BVNVerificationUrl = Constants.bvnVerificationURL + "/" + bvn;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<BVNVerificationResponse> responseEntity = restTemplate.exchange(
                BVNVerificationUrl,
                HttpMethod.GET,
                requestEntity,
                BVNVerificationResponse.class
        );
        return responseEntity;
    }

    private OpenAccountRequest getOpenAccountRequest(BVNVerificationResponse result) {
        String requestId = AppUtils.generateRequestID(21);
        OpenAccountRequest openAccountRequest = null;
        openAccountRequest = OpenAccountRequest.builder()
                .title(1)
                .surname(result.getLastName())
                .firstName(result.getFirstName())
                .otherName("")
                .gender(1)
                .dOB(result.getDateOfBirth())
                .address(result.getResidentialAddress())
                .phoneNumber(result.getPhoneNumber())
                .email(result.getEmail())
                .bVN(result.getBvn())
                .branchCode(branchCode)
                .productCode(productCode)
                .iDType(IDType)
                .iDCardNo(result.getNameOnCard())
                .iDIssueDate(result.getRegistrationDate())
                .iDExpiryDate(new Date())
                .referenceID(requestId)
                .acctOfficer(AcctOfficer)
                .build();
        return openAccountRequest;
    }

    @Override
    @Transactional
    public AccountBalanceResponse checkBalance(CheckBalanceRequest checkBalanceRequest) throws Throwable {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new MFBException("Invalid user"));

        final AccountBalanceResponse[] accountBalanceResponse = {null};

        TBLAccount account = tblAccountRepository.findTBLAccountByPhone(checkBalanceRequest.getPhoneNumber())
                .orElseThrow(() -> new MFBException("Account not found"));
        String accountNumber = account.getAccountnumber();
        log.info("Account number: {}", account.getAccountnumber());

        if (balanceConfiguration.isCheckBalanceEnabled()) {
            if (account.getBKBalance().compareTo(Constants.serviceFee) <= 0) {
                throw new MFBException("Insufficient account balance");
            }
        }
        ServicePaymentRequest servicePaymentRequest = getBalanceServiceChargeRequest(account, checkBalanceRequest);

        // CompletableFuture for getAccountBalance
        CompletableFuture<AccountBalanceResponse> getAccountBalanceFuture = CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<AccountBalanceResponse> response = getAccountBalance(accountNumber);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    accountBalanceResponse[0] = response.getBody();
                    log.info("Account balance info: {}", accountBalanceResponse[0]);
                }
            } catch (Exception ex) {
                throw new CompletionException("Failed check balance operation", ex);
            }
            return accountBalanceResponse[0];
        });

        // CompletableFuture for handleServiceFee
        CompletableFuture<Void> handleServiceFeeFuture = CompletableFuture.runAsync(() -> {
            try {
                handleServiceFee(servicePaymentRequest);
            } catch (Exception e) {
                throw new CompletionException("Failed processing of service charge", e);
            }
        });

        // Combine CompletableFuture instances
        CompletableFuture<Void> combinedFuture = getAccountBalanceFuture.thenComposeAsync(accountResponse ->
                handleServiceFeeFuture);

        // Join the CompletableFuture to get the result or handle any exceptions
        try {
            combinedFuture.join();
        } catch (CompletionException ex) {
            throw ex.getCause(); // Propagate the original exception
        }

        return accountBalanceResponse[0];
    }



    private void handleServiceFee(ServicePaymentRequest servicePaymentRequest) throws Exception {
       try {
           String servicePaymentUrl = Constants.servicePaymentUrl;
           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);
           HttpEntity<ServicePaymentRequest> request = new HttpEntity<>(servicePaymentRequest, headers);
           ResponseEntity<ServicePaymentResponse> paymentResponse = restTemplate.exchange(
                   servicePaymentUrl,
                   HttpMethod.POST,
                   request,
                   ServicePaymentResponse.class
           );
           if (paymentResponse.getStatusCode().is2xxSuccessful() && paymentResponse.getBody() != null) {
               ServicePaymentResponse servicePaymentResponse = paymentResponse.getBody();
               log.info("Service charge response {}",servicePaymentResponse);
           }
       }catch (Exception ex) {
           throw new Exception(ex);
       }
    }

    private ResponseEntity<AccountBalanceResponse> getAccountBalance(String accountNumber) {
        String accountBalanceURL = Constants.accountBalanceURL+accountNumber;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<AccountBalanceResponse> response = restTemplate.exchange(
                accountBalanceURL,
                HttpMethod.GET,
                request,
                AccountBalanceResponse.class
        );
        return response;
    }

    private ServicePaymentRequest getBalanceServiceChargeRequest(TBLAccount account, CheckBalanceRequest checkBalanceRequest) {
        String requestID = AppUtils.generateRequestID(21);
        String transactionHash = AppUtils.Sha512(account.getAccountnumber(), checkBalanceRequest.getCreditAcct(),
                checkBalanceRequest.getPayamount(), requestID, secretKey);
        return ServicePaymentRequest.builder()
                .debitAcct(account.getAccountnumber())
                .transactionHash(transactionHash)
                .payamount(checkBalanceRequest.getPayamount())
                .narration(checkBalanceRequest.getNarration())
                .creditAcct(checkBalanceRequest.getCreditAcct())
                .requestID(requestID)
                .build();
    }

    @Override
    @Transactional
    public IntraBankTransferResponse intraBankTransfer(TransferRequest transferRequest) throws Exception {
        log.info("transfer request {}",transferRequest);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username)
                .orElseThrow(() -> new MFBException("User not found"));

        IntraBankNameEnquiryResponse intraBankNameEnquiryResponse1 = null;
        IntraBankTransferResponse intraBankTransferResponse = null;
        BaseResponse<String> baseResponse = null;

        TBLAccount account = tblAccountRepository.findTBLAccountByPhone(transferRequest.getPhoneNumber())
                .orElseThrow(() -> new MFBException("Account not found"));
        log.info("debit account number: {}", account.getAccountnumber());

        log.info("credit acc {}",transferRequest.getCreditAcct());
        //Name enquiry API call
        try {
            ResponseEntity<IntraBankNameEnquiryResponse> nameEnquiryResponse = getIntraBankNameEnquiry(transferRequest.getCreditAcct());
            if (nameEnquiryResponse.getStatusCode().is2xxSuccessful() && nameEnquiryResponse.getBody() != null) {
                intraBankNameEnquiryResponse1 = nameEnquiryResponse.getBody();
                log.info("NameEnquiryResponse info: {}", intraBankNameEnquiryResponse1);

                if (!intraBankNameEnquiryResponse1.getResponse().getAccountNo().equals(transferRequest.getCreditAcct())) {
                    throw new MFBException("Credit account details not found");
                }
            }
        } catch (Exception ex) {
            throw new Exception("Failed name enquiry operation", ex);
        }

        IntraBankTransferRequest intraBankTransferRequest = getIntraBankTransferRequest(transferRequest, account);

        BigDecimal balance = account.getBKBalance();
        if (balanceConfiguration.isCheckBalanceEnabled()) {
            if (account.getBKBalance().compareTo(Constants.serviceFee) <= 0) {
                throw new MFBException("Insufficient account balance");
            }
        }
        //IntraBank transfer API Call
        try {
            ResponseEntity<IntraBankTransferResponse> response = handleIntraBankTransfer(intraBankTransferRequest);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                intraBankTransferResponse = response.getBody();
                log.info("intra bank transfer dto {}",intraBankTransferResponse);
            }
        } catch (Exception ex) {
            throw new Exception("Failed intra transfer operation", ex);
        }
        return intraBankTransferResponse;
    }

    private ResponseEntity<IntraBankTransferResponse> handleIntraBankTransfer(IntraBankTransferRequest intraBankTransferRequest) {
        String intraBankTransferUrl = Constants.intraBankTransferUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<IntraBankTransferRequest> request = new HttpEntity<>(intraBankTransferRequest, headers);
        ResponseEntity<IntraBankTransferResponse> response = restTemplate.exchange(
                intraBankTransferUrl,
                HttpMethod.POST,
                request,
                IntraBankTransferResponse.class
        );
        return response;
    }

    private ResponseEntity<IntraBankNameEnquiryResponse> getIntraBankNameEnquiry(String accountNumber) {
        String nameEnquiryUrl = Constants.intraBankNameEnquiryUrl+accountNumber;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<IntraBankNameEnquiryResponse> response = restTemplate.exchange(
                nameEnquiryUrl,
                HttpMethod.GET,
                request,
                IntraBankNameEnquiryResponse.class
        );
        return response;
    }

    private IntraBankTransferRequest getIntraBankTransferRequest(TransferRequest transferRequest, TBLAccount account) {
        String requestID = AppUtils.generateRequestID(21);
        String transactionHash = AppUtils.Sha512(account.getAccountnumber(), transferRequest.getCreditAcct(),
                transferRequest.getPayamount(), requestID, secretKey);
        return IntraBankTransferRequest.builder()
                .debitAcct(account.getAccountnumber())
                .narration(transferRequest.getNarration())
                .payamount(transferRequest.getPayamount())
                .creditAcct(transferRequest.getCreditAcct())
                .requestID(requestID)
                .transactionHash(transactionHash)
                .build();
    }



    @Override
    @Transactional
    public InterBankTransferResponse interBankTransfer(InterBankTrasferRequest interBankTrasferRequest) throws Exception {
        //Fetch user and account detail
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username)
                .orElseThrow(() -> new MFBException("User not found"));

        log.info("incoming request {}",interBankTrasferRequest);

        InterBankNameEnquiryResponse interBankNameEnquiryResponse = null;
        InterBankTransferResponse interBankTransferResponse = null;

        TBLAccount account = tblAccountRepository.findTBLAccountByPhone(interBankTrasferRequest.getPhoneNumber())
                .orElseThrow(() -> new MFBException("Account not found"));
        log.info("debit account number: {}", account.getAccountnumber());

        NameEnquiryRequest nameEnquiryRequest = NameEnquiryRequest.builder()
                .accountNumber(interBankTrasferRequest.getBeneficiaryAccount())
                .bankCode(interBankTrasferRequest.getBankCode())
                .build();
        //Inter bank Name enquiry API call
        try {
            ResponseEntity<InterBankNameEnquiryResponse> nameEnquiryResponse = getInterBankNameEnquiry(nameEnquiryRequest);
            if (nameEnquiryResponse.getStatusCode().is2xxSuccessful() && nameEnquiryResponse.getBody() != null) {
                interBankNameEnquiryResponse = nameEnquiryResponse.getBody();
                log.info("NameEnquiryResponse info: {}", nameEnquiryResponse.getBody());

                if (!interBankNameEnquiryResponse.getAccountNumber().equals(interBankTrasferRequest.getBeneficiaryAccount())) {
                    throw new MFBException("Credit account details not found");
                }
            }
        } catch (Exception ex) {
            throw new Exception("Failed name enquiry operation", ex);
        }

        assert interBankNameEnquiryResponse != null;
        InterTransferRequest interTransferRequest = getInterTransferRequest(interBankTrasferRequest, account);

        BigDecimal balance = account.getBKBalance();
        if (balanceConfiguration.isCheckBalanceEnabled()) {
            if (account.getBKBalance().compareTo(Constants.serviceFee) <= 0) {
                throw new MFBException("Insufficient account balance");
            }
        }
        //Inter bank API Call
        try {
            ResponseEntity<InterBankTransferResponse> res = handleInterBankTranfer(interTransferRequest);
            if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                interBankTransferResponse = res.getBody();
                log.info("Interbank transfer info: {}", interBankTransferResponse);
            }
        } catch (Exception ex) {
            throw new Exception("Failed name enquiry operation", ex);
        }
        return interBankTransferResponse;
    }

    private ResponseEntity<InterBankNameEnquiryResponse> getInterBankNameEnquiry(NameEnquiryRequest nameEnquiryRequest) {
        String nameEnquiryUrl = Constants.interBankNameEnquiryUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NameEnquiryRequest> request = new HttpEntity<>(nameEnquiryRequest, headers);
        ResponseEntity<InterBankNameEnquiryResponse> response = restTemplate.exchange(
                nameEnquiryUrl,
                HttpMethod.POST,
                request,
                InterBankNameEnquiryResponse.class
        );
        return response;
    }

    private ResponseEntity<InterBankTransferResponse> handleInterBankTranfer(InterTransferRequest interTransferRequest) throws Exception {
       try {
           String interBankTransferUrl = Constants.interBankTransferUrl;
           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);
           HttpEntity<InterTransferRequest> request = new HttpEntity<>(interTransferRequest, headers);
           ResponseEntity<InterBankTransferResponse> response = restTemplate.exchange(
                   interBankTransferUrl,
                   HttpMethod.POST,
                   request,
                   InterBankTransferResponse.class
           );
           return response;
       }catch (Exception ex) {
           throw new Exception(ex);
       }
    }

    private InterTransferRequest getInterTransferRequest(InterBankTrasferRequest interBankTrasferRequest, TBLAccount account) {
        String transRef = AppUtils.generateRequestID(21);
        return InterTransferRequest.builder()
                .narration(interBankTrasferRequest.getNarration())
                .amount(interBankTrasferRequest.getAmount())
                .debitAcct(account.getAccountnumber())
                .sourceAccount(account.getAccountnumber())
                .beneficiaryAccount(interBankTrasferRequest.getBeneficiaryAccount())
                .beneficiaryBank(interBankTrasferRequest.getBeneficiaryBank())
                .checkSum(interBankTrasferRequest.getCheckSum())
                .transRef(transRef)
                .build();
    }
    @Override
    public StatementResponse miniStatement(AccountRequest accountRequest) throws Throwable {
        log.info("Account request {}",accountRequest);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username)
                .orElseThrow(() -> new MFBException("Invalid user"));

        final StatementResponse[] statementResponse = {null};

        TBLAccount account = tblAccountRepository.findTBLAccountByPhone(accountRequest.getPhoneNumber())
                .orElseThrow(() -> new MFBException("Invalid account"));
        log.info("debit account number: {}", account.getAccountnumber());

        if (balanceConfiguration.isCheckBalanceEnabled()) {
            if (account.getBKBalance().compareTo(Constants.serviceFee) <= 0) {
                throw new MFBException("Insufficient account balance");
            }
        }
        // CompletableFuture for account statement
        CompletableFuture<StatementResponse> getStatementFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("incoming request {}",accountRequest);
                ResponseEntity<StatementResponse> response = handleStatementReport(account.getAccountnumber(),
                        accountRequest.getStartDate(), accountRequest.getEndDate());

                log.info("response body {}",response.getBody());
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    statementResponse[0] = response.getBody();
                    handleNullDates(statementResponse);
                    log.info("StatementResponse info: {}", statementResponse[0]);
                }
            } catch (Exception ex) {
                throw new CompletionException("Failed statement report operation", ex);
            }
            return statementResponse[0];
        });

        ServicePaymentRequest servicePaymentRequest = getStatementServiceChargeRequest(account, accountRequest);

        // CompletableFuture for handleServiceFee
        CompletableFuture<Void> handleServiceFeeFuture = CompletableFuture.runAsync(() -> {
            try {
                handleServiceFee(servicePaymentRequest);
            } catch (Exception e) {
                throw new CompletionException("Failed processing of service charge", e);
            }
        });
        // Combine CompletableFuture instances
        CompletableFuture<Void> combinedFuture = getStatementFuture.thenComposeAsync(statementRes ->
                handleServiceFeeFuture);

        // Join the CompletableFuture to get the result or handle any exceptions
        try {
            combinedFuture.join();
        } catch (CompletionException ex) {
            throw ex.getCause(); // Propagate the original exception
        }
        return statementResponse[0];
    }

    private void handleNullDates(StatementResponse[] statementResponse) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-mm-yyyy");
        if (statementResponse[0].getResponse() != null) {
            for (StatementResponse.ResponseData responseData : statementResponse[0].getResponse()) {
                if (responseData.getTranDate() == null) {
                    responseData.setTranDate(LocalDate.now().format(formatter));
                }
                if (responseData.getValueDate() == null) {
                    responseData.setValueDate(LocalDate.now().format(formatter));
                }
            }
        }
    }

    private ServicePaymentRequest getStatementServiceChargeRequest(TBLAccount account, AccountRequest accountRequest) {
        String requestID = AppUtils.generateRequestID(21);
        String transactionHash = AppUtils.Sha512(account.getAccountnumber(), accountRequest.getCreditAcct(),
                accountRequest.getPayamount(), requestID, secretKey);
        return ServicePaymentRequest.builder()
                .debitAcct(account.getAccountnumber())
                .transactionHash(transactionHash)
                .payamount(accountRequest.getPayamount())
                .narration(accountRequest.getNarration())
                .creditAcct(accountRequest.getCreditAcct())
                .requestID(requestID)
                .build();
    }

    private ResponseEntity<StatementResponse> handleStatementReport(String AccountNo, String StartDate, String EndDate) {
        String accountStatementUrl = Constants.accountStatementUrl+"AccountNo=" + AccountNo + "&StartDate=" +
                StartDate + "&EndDate=" + EndDate;
        log.info("account url {}", accountStatementUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<StatementResponse> response = restTemplate.exchange(
                accountStatementUrl,
                HttpMethod.GET,
                request,
                StatementResponse.class
        );
        log.info(String.valueOf(response.getBody()));
        return response;
    }


}
