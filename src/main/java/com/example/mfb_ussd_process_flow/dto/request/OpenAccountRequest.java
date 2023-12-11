package com.example.mfb_ussd_process_flow.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OpenAccountRequest {
    private int title;
    private String surname;
    private String firstName;
    private String otherName;
    private int gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date dOB;

    private String address;
    private String phoneNumber;
    private String email;
    @JsonProperty("bVN")
    private String bVN;
    private String branchCode;
    private String productCode;
    @JsonProperty("iDType")
    private int iDType;
    @JsonProperty("iDCardNo")
    private String iDCardNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date iDIssueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date iDExpiryDate;

    private String acctOfficer;
    private String referenceID;


    public void setbVN(String bVN) {
        this.bVN = bVN;
    }

    public void setiDType(int iDType) {
        this.iDType = iDType;
    }

    public void setiDCardNo(String iDCardNo) {
        this.iDCardNo = iDCardNo;
    }

    public void setiDExpiryDate(Date iDExpiryDate) {
        this.iDExpiryDate = iDExpiryDate;
    }

    // Use this method if you want to set a default value
    public void setDefaultIDExpiryDate() {
        long currentTime = System.currentTimeMillis();
        long oneHundredYearsInMillis = 100L * 365 * 24 * 60 * 60 * 1000;
        this.iDExpiryDate = new Date(currentTime + oneHundredYearsInMillis);
    }
}
