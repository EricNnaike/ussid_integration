package com.example.mfb_ussd_process_flow.dto.response;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivateUserResponse extends BaseResponse{
    private String username;
    private String secretKey;
    private boolean enabled;
    private String password;
}
