package com.example.mfb_ussd_process_flow.service;

import com.example.mfb_ussd_process_flow.config.JwtService;
import com.example.mfb_ussd_process_flow.dto.request.ActivateUserRequest;
import com.example.mfb_ussd_process_flow.dto.request.LoginRequest;
import com.example.mfb_ussd_process_flow.dto.request.UserRequest;
import com.example.mfb_ussd_process_flow.dto.response.ActivateUserResponse;
import com.example.mfb_ussd_process_flow.dto.response.LoginResponse;
import com.example.mfb_ussd_process_flow.dto.response.UserResponse;
import com.example.mfb_ussd_process_flow.entityUser.Role;
import com.example.mfb_ussd_process_flow.entityUser.Token;
import com.example.mfb_ussd_process_flow.entityUser.Users;
import com.example.mfb_ussd_process_flow.enums.RoleType;
import com.example.mfb_ussd_process_flow.enums.StatusConstant;
import com.example.mfb_ussd_process_flow.enums.TokenType;
import com.example.mfb_ussd_process_flow.exceptions.MFBException;
import com.example.mfb_ussd_process_flow.repositoryUser.RoleRepository;
import com.example.mfb_ussd_process_flow.repositoryUser.TokenRepository;
import com.example.mfb_ussd_process_flow.repositoryUser.UserRepository;
import com.example.mfb_ussd_process_flow.service.emailService.EmailService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.security.KeyPairGenerator;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository repositoryUser;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;

    @Override
    public UserResponse registerUser(UserRequest userRequest) throws MFBException, MessagingException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = repositoryUser.findByUsername(username)
                .orElseThrow(() -> new MFBException("Invalid user"));

        if (!user.getRole().equals(RoleType.ADMIN)) {
            throw new MFBException("Only an admin is permitted to perform this operation");
        }else {
            if (repositoryUser.existsByUsername(userRequest.getUsername().toLowerCase()))
                throw new MFBException("Username: "+ userRequest.getUsername() +" already exist");

            String secretKey = secretKey();

            Users users = storeUser(userRequest, secretKey);
            Users savedUser = repositoryUser.save(users);
            emailService.sendRegistrationNotification(users);

            UserResponse response = new UserResponse();
            response.setUsername(savedUser.getUsername());
            response.setId(savedUser.getId());
            response.setMessage("User created successfully. " +
                    "Kindly activate your account with the credentials forwarded to your mail.");
            response.setSecretKey(savedUser.getSecretKey());

            return response;
        }

    }

    @Override
    public void registerAdmin(UserRequest userRequest) throws MFBException {
        if (repositoryUser.existsByUsername(userRequest.getUsername().toLowerCase())) throw new MFBException("Invalid user");

        Users users = storeAdminUser(userRequest);
        Users savedUser = repositoryUser.save(users);

        UserResponse response = new UserResponse();
        response.setUsername(savedUser.getUsername());
        response.setMessage("User created successfully");
        response.setSecretKey(savedUser.getSecretKey());
    }

    @Override
    public ActivateUserResponse activateUser(ActivateUserRequest activateUserRequest) throws MFBException, MessagingException {
        Users user = repositoryUser.findByUsername(activateUserRequest.getUsername())
                .orElseThrow(() -> new MFBException("User not found"));

        if (user.getStatus().equals(StatusConstant.INACTIVE)) {
            if (user.getSecretKey().equals(activateUserRequest.getSecretKey())) {
                String password = passwordService.generatePassword();

                user.setPassword(passwordEncoder.encode(password));
                user.setStatus(StatusConstant.ACTIVE);
                user.setEnabled(true);

                repositoryUser.save(user);
                emailService.sendActivationNotification(user, password);

                ActivateUserResponse response = new ActivateUserResponse();
                response.setUsername(user.getUsername());
                response.setMessage("User activated successfully");
                response.setSecretKey(user.getSecretKey());
                response.setEnabled(user.isEnabled());
                response.setPassword(password);

                return response;
            }else {
                throw new MFBException("Secrete key mismatch");
            }
        } else {
            throw new MFBException("User is enabled already");
        }

    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) throws MFBException {
        LoginResponse response = new LoginResponse();
        Users user = repositoryUser.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new MFBException("Invalid user"));

        if (user.getStatus().equals(StatusConstant.INACTIVE) || !user.isEnabled()) throw new MFBException("User IS not validated");
//        if (!user.getPassword().equals(loginRequest.getPassword())) throw new MFBException("Invalid password");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            String token = jwtService.generateToken(user);

            this.revokeAllUserToken(user);
            this.savedUserToken(user, token);

            response.setSecretKey(user.getSecretKey());
            response.setUsername(user.getUsername());
            response.setMessage("Login Successful");
            response.setId(user.getId());
            response.setAccessToken(token);
            response.setTokenExpirationDate(jwtService.getExpirationDate(token));

        } catch (BadCredentialsException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }

    private boolean validateUser(Users user, LoginRequest loginRequest) {
        if (user != null && loginRequest != null) {
           String encryptedPassword = passwordEncoder.encode(loginRequest.getPassword());
            return user.getUsername().equalsIgnoreCase(loginRequest.getUsername()) && user.getPassword().equals(encryptedPassword);
        }
        return false;
    }

    @Override
    @Transactional
    public Role createRole(String name) {
        return roleRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(name);
            roleRepository.save(newRole);
            return newRole;
        });
    }

    @Override
    public List<String> getRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(Role::getName).collect(Collectors.toList());
    }

    private void revokeAllUserToken(Users user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void savedUserToken(Users user, String token) {
        Token savedToken = Token.builder()
                        .users(user)
                                .token(token)
                                        .tokenType(TokenType.BEARER)
                                                .expired(false)
                                                        .revoked(false)
                                                                .build();
        tokenRepository.save(savedToken);

    }

    private Users storeUser(UserRequest userRequest, String secretKey) {
        return Users.builder()
                .username(userRequest.getUsername())
                .secretKey(secretKey)
                .createdAt(LocalDateTime.now())
                .status(StatusConstant.INACTIVE)
                .role(RoleType.GENERAL_USER)
                .build();
    }

    private Users storeAdminUser(UserRequest userRequest) {
        return Users.builder()
                .username(userRequest.getUsername())
                .createdAt(LocalDateTime.now())
                .status(StatusConstant.ACTIVE)
                .enabled(true)
                .role(RoleType.ADMIN)
                .password(passwordEncoder.encode("admin@254"))
                .build();
    }

    private String secretKey() {
        String key = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(512);
            byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
            key = Arrays.toString(publicKey);
            key = passwordEncoder.encode(key);
            key = key.replaceAll("[/]", "v");
        } catch (Exception e) {
            System.out.printf(e.getMessage());
        }
        return key;
    }


}
