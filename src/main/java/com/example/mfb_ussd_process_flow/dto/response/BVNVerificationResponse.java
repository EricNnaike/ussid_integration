package com.example.mfb_ussd_process_flow.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BVNVerificationResponse {
    @JsonProperty("responseCode")
    private String responseCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("bankCode")
    private String bankCode;

    @JsonProperty("bankBranch")
    private String bankBranch;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date registrationDate;

    @JsonProperty("watchListed")
    private boolean watchListed;

    @JsonProperty("bvn")
    private String bvn;

    @JsonProperty("email")
    private String email;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("secondaryPhoneNumber")
    private String secondaryPhoneNumber;

    @JsonProperty("levelOfAccount")
    private String levelOfAccount;

    @JsonProperty("lgaOfResidence")
    private String lgaOfResidence;

    @JsonProperty("maritalStatus")
    private String maritalStatus;

    @JsonProperty("nationalIdentityNumber")
    private String nationalIdentityNumber;

    @JsonProperty("nameOnCard")
    private String nameOnCard;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("residentialAddress")
    private String residentialAddress;

    @JsonProperty("stateOfOrigin")
    private String stateOfOrigin;

    @JsonProperty("stateOfResidence")
    private String stateOfResidence;

    @JsonProperty("lgaOfOrigin")
    private String lgaOfOrigin;

}
