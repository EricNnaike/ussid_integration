package com.example.mfb_ussd_process_flow.config;

import com.example.mfb_ussd_process_flow.dto.request.UserRequest;
import com.example.mfb_ussd_process_flow.entityUser.Role;
import com.example.mfb_ussd_process_flow.enums.RoleType;
import com.example.mfb_ussd_process_flow.repositoryUser.UserRepository;
import com.example.mfb_ussd_process_flow.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class MasterRecordsLoader {

    private final TransactionTemplate transactionTemplate;
    private final UserService userService;
    private final UserRepository repositoryUser;

    @Value("${adminLogin.email}")
    private String adminEmail;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        log.info("STARTING...........");
        transactionTemplate.execute(tx -> {
            try {
                loadDefaults();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private void loadDefaults(){

        Role role = userService.createRole(RoleType.GENERAL_USER.getAbbreviate());
        Role role1 = userService.createRole(RoleType.ADMIN.getAbbreviate());
        Role role3 = userService.createRole(RoleType.SUPER_ADMIN.getAbbreviate());


        UserRequest adminDto = new UserRequest();
        adminDto.setUsername(adminEmail);
        adminDto.setEmail(adminEmail);

        createAdminUser(adminDto);

    }

    private void createAdminUser(UserRequest dto) {
        repositoryUser.findByUsername(dto.getUsername())
                .orElseGet(() -> {
                    log.info("===========CREATING ADMIN {} ============", dto.getUsername());
                    try {
                        userService.registerAdmin(dto);
                    } catch (Exception e) {
                        System.out.printf(e.getMessage());
                    }
                    return null;
                });
    }



}
