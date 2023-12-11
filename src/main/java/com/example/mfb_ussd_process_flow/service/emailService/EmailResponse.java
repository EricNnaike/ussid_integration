package com.example.mfb_ussd_process_flow.service.emailService;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;


@Getter
@Setter
@Validated
public class EmailResponse {

    @NotEmpty
    private String message;

    private Boolean status;
}
