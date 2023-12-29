package com.example.mfb_ussd_process_flow.service;

import com.example.mfb_ussd_process_flow.config.BalanceConfiguration;
import com.example.mfb_ussd_process_flow.dto.request.*;
import com.example.mfb_ussd_process_flow.dto.response.*;
import com.example.mfb_ussd_process_flow.entityAccount.TBLAccount;
import com.example.mfb_ussd_process_flow.entityUser.Users;
import com.example.mfb_ussd_process_flow.exceptions.MFBException;
import com.example.mfb_ussd_process_flow.repositoryAccount.CustomerRepository;
import com.example.mfb_ussd_process_flow.repositoryAccount.TBLAccountRepository;
import com.example.mfb_ussd_process_flow.repositoryUser.TransactionHistoryRepository;
import com.example.mfb_ussd_process_flow.repositoryUser.UserRepository;
import com.example.mfb_ussd_process_flow.utils.AppUtils;
import lombok.Data;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
@Slf4j
@Data
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final WebClient.Builder webClient;
    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final TBLAccountRepository tblAccountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final BalanceConfiguration balanceConfiguration;


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

    @Value("${interBankTransfer.secretKey}")
    private String interTransferSecretKey;

    @Value("${interBankTransfer.clientKey}")
    private String interTransferClientKey;

    @Value("${serviceCharge.payAmount}")
    private BigDecimal serviceChargePayAmount;

    @Value("${serviceCharge.creditAccount}")
    private String serviceChargeCreditAccount;

    @Value("${serviceCharge.balanceNaration}")
    private String balanceNaration;

    @Value("${serviceCharge.statementNaration}")
    private String statementNaration;

    @Value("${api.bvnVerificationURL}")
    private String bvnVerificationURL;

    @Value("${api.openAccountURL}")
    private String openAccountURL;

    @Value("${api.accountBalanceURL}")
    private String accountBalanceURLs;

    @Value("${api.servicePaymentUrl}")
    private String servicePaymentsUrl;

    @Value("${api.intraBankTransferUrl}")
    private String intraBankTransferUrls;

    @Value("${api.intraBankNameEnquiryUrl}")
    private String intraBankNameEnquiryUrl;

    @Value("${api.interBankNameEnquiryUrl}")
    private String interBankNameEnquiryUrl;

    @Value("${api.interBankTransferUrl}")
    private String interBankTransferUrls;

    @Value("${api.accountStatementUrl}")
    private String accountStatementUrls;




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
        String openAccountUrl = openAccountURL ;
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
        String BVNVerificationUrl = bvnVerificationURL + "/" + bvn;
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
        public BalanceResponse checkBalance(CheckBalanceRequest checkBalanceRequest) throws Throwable {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new MFBException("Invalid user"));

        final AccountBalanceResponse[] accountBalanceResponse = {null};

        log.info("Account number: {}", checkBalanceRequest.getAccountNumber());
        ServicePaymentRequest servicePaymentRequest = getBalanceServiceChargeRequest(checkBalanceRequest.getAccountNumber());

        CompletableFuture<AccountBalanceResponse> getAccountBalanceFuture = CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<AccountBalanceResponse> response = getAccountBalance(checkBalanceRequest.getAccountNumber());
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    accountBalanceResponse[0] = response.getBody();
                    log.info("Account balance info: {}", accountBalanceResponse[0]);
                }
            } catch (Exception ex) {
                throw new CompletionException("Failed check balance operation", ex);
            }
            return accountBalanceResponse[0];
        });

        CompletableFuture<Void> handleServiceFeeFuture = CompletableFuture.runAsync(() -> {
            try {
                handleServiceFee(servicePaymentRequest);
            } catch (Exception e) {
                throw new CompletionException("Failed processing of service charge", e);
            }
        });

        CompletableFuture<Void> combinedFuture = getAccountBalanceFuture.thenComposeAsync(accountResponse ->
                handleServiceFeeFuture);

        try {
            combinedFuture.join();
        } catch (CompletionException ex) {
            throw ex.getCause();
        }

        BalanceResponse balanceResponse = getAccountBalanceResponse(accountBalanceResponse, checkBalanceRequest.getAccountNumber());
        return balanceResponse;
    }

    private BalanceResponse getAccountBalanceResponse(AccountBalanceResponse[] accountBalanceResponse, String selectedAccountNumber) {
        BalanceResponse.Response res = new BalanceResponse.Response();
        res.setAvailableBalance(accountBalanceResponse[0].getResponse().getAvailableBalance());
        res.setRetval(accountBalanceResponse[0].getResponse().getRetval());
        res.setUsableBalance(accountBalanceResponse[0].getResponse().getUsableBalance());
        res.setRetmsg(accountBalanceResponse[0].getResponse().getRetmsg());
        res.setAcctName(accountBalanceResponse[0].getResponse().getAcctName());
        res.setAccountNumber(selectedAccountNumber);

        return BalanceResponse.builder()
                .Status(accountBalanceResponse[0].getStatus())
                .Response(res)
                .build();
    }


    private void handleServiceFee(ServicePaymentRequest servicePaymentRequest) throws Exception {
       try {
           String servicePaymentUrl = servicePaymentsUrl;
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
        String accountBalanceURL = accountBalanceURLs+accountNumber;
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

    private ServicePaymentRequest getBalanceServiceChargeRequest(String accountNumber) {
        String requestID = AppUtils.generateRequestID(21);
        String transactionHash = AppUtils.Sha512(accountNumber, serviceChargeCreditAccount,
                serviceChargePayAmount, requestID, secretKey);
        return ServicePaymentRequest.builder()
                .debitAcct(accountNumber)
                .transactionHash(transactionHash)
                .payamount(serviceChargePayAmount)
                .narration(balanceNaration)
                .creditAcct(serviceChargeCreditAccount)
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
            if (account.getBKBalance().compareTo(serviceChargePayAmount) <= 0) {
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

    @Override
    @Transactional
    public IntraBankTransferResponse intraBankTransferMethod(TransferRequestDto transferRequestDto) throws Exception {
        log.info("transfer request {}",transferRequestDto);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username)
                .orElseThrow(() -> new MFBException("User not found"));

        IntraBankNameEnquiryResponse intraBankNameEnquiryResponse1 = null;
        IntraBankTransferResponse intraBankTransferResponse = null;
        BaseResponse<String> baseResponse = null;

        log.info("debit account number: {}", transferRequestDto.getAccountNumber());

        log.info("credit acc {}",transferRequestDto.getCreditAcct());
        //Name enquiry API call
        try {
            ResponseEntity<IntraBankNameEnquiryResponse> nameEnquiryResponse = getIntraBankNameEnquiry(transferRequestDto.getCreditAcct());
            if (nameEnquiryResponse.getStatusCode().is2xxSuccessful() && nameEnquiryResponse.getBody() != null) {
                intraBankNameEnquiryResponse1 = nameEnquiryResponse.getBody();
                log.info("NameEnquiryResponse info: {}", intraBankNameEnquiryResponse1);

                if (!intraBankNameEnquiryResponse1.getResponse().getAccountNo().equals(transferRequestDto.getCreditAcct())) {
                    throw new MFBException("Credit account details not found");
                }
            }
        } catch (Exception ex) {
            throw new Exception("Failed name enquiry operation", ex);
        }

        IntraBankTransferRequest intraBankTransferRequest = getIntraBankTransferRequestDto(transferRequestDto, transferRequestDto.getAccountNumber());

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
        String intraBankTransferUrl = intraBankTransferUrls;
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
        String nameEnquiryUrl = intraBankNameEnquiryUrl+accountNumber;
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

    private IntraBankTransferRequest getIntraBankTransferRequest(TransferRequest transferRequest, TBLAccount account ) {
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
    private IntraBankTransferRequest getIntraBankTransferRequestDto(TransferRequestDto transferRequest, String accountNumber ) {
        String requestID = AppUtils.generateRequestID(21);
        String transactionHash = AppUtils.Sha512(accountNumber, transferRequest.getCreditAcct(),
                transferRequest.getPayamount(), requestID, secretKey);
        return IntraBankTransferRequest.builder()
                .debitAcct(accountNumber)
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
                .bankCode(interBankTrasferRequest.getBeneficiaryBank())
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
            if (account.getBKBalance().compareTo(serviceChargePayAmount) <= 0) {
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
        String nameEnquiryUrl = interBankNameEnquiryUrl;
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
           String interBankTransferUrl = interBankTransferUrls;
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
        BigDecimal roundedAmount = interBankTrasferRequest.getAmount().setScale(2, RoundingMode.HALF_UP);
        String checkSum = AppUtils.checkSumSha512(interTransferClientKey, account.getAccountnumber(),
                interBankTrasferRequest.getBeneficiaryAccount(), transRef,
                String.valueOf(roundedAmount), interTransferSecretKey);
        String encodedCheckSum = Base64.getEncoder().encodeToString(checkSum.getBytes());

        return InterTransferRequest.builder()
                .narration(interBankTrasferRequest.getNarration())
                .amount(interBankTrasferRequest.getAmount())
                .debitAcct(account.getAccountnumber())
                .sourceAccount(account.getAccountnumber())
                .beneficiaryAccount(interBankTrasferRequest.getBeneficiaryAccount())
                .beneficiaryBank(interBankTrasferRequest.getBeneficiaryBank())
                .checkSum(encodedCheckSum)
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

        // CompletableFuture for account statement
        CompletableFuture<StatementResponse> getStatementFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("incoming request {}",accountRequest);
                ResponseEntity<StatementResponse> response = handleStatementReport(accountRequest.getAccountNumber(),
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

        ServicePaymentRequest servicePaymentRequest = getStatementServiceChargeRequest(accountRequest.getAccountNumber(), accountRequest);

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

    private ServicePaymentRequest getStatementServiceChargeRequest(String accountNumber, AccountRequest accountRequest) {
        String requestID = AppUtils.generateRequestID(21);
        String transactionHash = AppUtils.Sha512(accountNumber, serviceChargeCreditAccount,
                serviceChargePayAmount, requestID, secretKey);
        return ServicePaymentRequest.builder()
                .debitAcct(accountNumber)
                .transactionHash(transactionHash)
                .payamount(serviceChargePayAmount)
                .narration(statementNaration)
                .creditAcct(serviceChargeCreditAccount)
                .requestID(requestID)
                .build();
    }

    private ResponseEntity<StatementResponse> handleStatementReport(String AccountNo, String StartDate, String EndDate) {
        String accountStatementUrl = accountStatementUrls+"AccountNo=" + AccountNo + "&StartDate=" +
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

    @Override
    public List<AccountNumberResponse> findAccountNumber(RetrieveAccountRequest request) throws MFBException {
        log.info("phone number {}", request.getPhoneNumber());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username)
                .orElseThrow(() -> new MFBException("User not found"));

        List<String> accountNumberList = tblAccountRepository.findAllByPhone(request.getPhoneNumber());

        if (accountNumberList.isEmpty()) {
            throw new MFBException("No account numbers found for the given phone number.");
        }
        log.info("size {}", accountNumberList.size());

        List<AccountNumberResponse> accountNumberResponseList = new ArrayList<>();

        for (String accountNumber : accountNumberList) {
            AccountNumberResponse accountNumberResponse = new AccountNumberResponse();
            accountNumberResponse.setAccountNumber(accountNumber);
            accountNumberResponseList.add(accountNumberResponse);
        }

        return accountNumberResponseList;
    }


}
