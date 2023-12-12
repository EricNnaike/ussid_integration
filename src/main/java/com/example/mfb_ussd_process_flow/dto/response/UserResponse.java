package com.example.mfb_ussd_process_flow.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends BaseResponse{
    private String username;
    private String secretKey;
    private Long id;

}
