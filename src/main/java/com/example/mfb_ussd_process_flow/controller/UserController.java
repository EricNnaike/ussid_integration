package com.example.mfb_ussd_process_flow.controller;

import com.example.mfb_ussd_process_flow.dto.request.ActivateUserRequest;
import com.example.mfb_ussd_process_flow.dto.request.LoginRequest;
import com.example.mfb_ussd_process_flow.dto.request.UserRequest;
import com.example.mfb_ussd_process_flow.dto.response.ActivateUserResponse;
import com.example.mfb_ussd_process_flow.dto.response.LoginResponse;
import com.example.mfb_ussd_process_flow.dto.response.UserResponse;
import com.example.mfb_ussd_process_flow.exceptions.MFBException;
import com.example.mfb_ussd_process_flow.service.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@RequestBody UserRequest userRequest)
            throws MFBException, MessagingException {
        return userService.registerUser(userRequest);
    }

    @PostMapping("/user/activate")
    @ResponseStatus(HttpStatus.OK)
    public ActivateUserResponse activateUser(@RequestBody ActivateUserRequest activateUserRequest)
            throws MFBException, MessagingException {
        return userService.activateUser(activateUserRequest);
    }

    @PostMapping("/user/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginUser(@RequestBody LoginRequest loginRequest) throws MFBException {
        return userService.loginUser(loginRequest);
    }




}
