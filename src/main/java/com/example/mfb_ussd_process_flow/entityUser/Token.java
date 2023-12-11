package com.example.mfb_ussd_process_flow.entityUser;

import com.example.mfb_ussd_process_flow.entityUser.Users;
import com.example.mfb_ussd_process_flow.enums.TokenType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ussid_token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "tokenType")
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;

    @JsonBackReference
    @ManyToOne
    private Users users;


}
