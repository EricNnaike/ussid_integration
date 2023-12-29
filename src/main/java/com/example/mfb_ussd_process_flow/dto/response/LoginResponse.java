package com.example.mfb_ussd_process_flow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse extends BaseResponse{
    private Long id;

    private String username;
    private String email;

    private String secretKey;

    private String accessToken;

    private Date tokenExpirationDate;
}
