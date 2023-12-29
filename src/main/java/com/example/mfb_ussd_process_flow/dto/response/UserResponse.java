package com.example.mfb_ussd_process_flow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends BaseResponse{
    private String username;
    private String email;
    private String secretKey;
    private Long id;

}
