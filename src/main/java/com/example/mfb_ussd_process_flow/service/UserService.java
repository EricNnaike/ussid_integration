package com.example.mfb_ussd_process_flow.service;

import com.example.mfb_ussd_process_flow.dto.request.ActivateUserRequest;
import com.example.mfb_ussd_process_flow.dto.request.LoginRequest;
import com.example.mfb_ussd_process_flow.dto.request.UserRequest;
import com.example.mfb_ussd_process_flow.dto.response.ActivateUserResponse;
import com.example.mfb_ussd_process_flow.dto.response.LoginResponse;
import com.example.mfb_ussd_process_flow.dto.response.UserResponse;
import com.example.mfb_ussd_process_flow.entityUser.Role;
import com.example.mfb_ussd_process_flow.exceptions.MFBException;
import jakarta.mail.MessagingException;

import java.util.List;

public interface UserService {
    UserResponse registerUser(UserRequest userRequest) throws MFBException, MessagingException;
    void registerAdmin(UserRequest userRequest) throws MFBException;
    ActivateUserResponse activateUser(ActivateUserRequest activateUserRequest) throws MFBException, MessagingException;
    LoginResponse loginUser(LoginRequest loginRequest) throws MFBException;
    Role createRole(String name);
    List<String> getRoles();



}
