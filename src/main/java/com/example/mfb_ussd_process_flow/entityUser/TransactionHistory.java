package com.example.mfb_ussd_process_flow.entityUser;

import com.example.mfb_ussd_process_flow.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "transaction_history")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "creditAcct")
    private String creditAcct;

    @Column(name = "payamount")
    private BigDecimal payamount;

    @Column(name = "tellerno")
    private String tellerno;

    @Column(name = "narration")
    private String narration;

    @Column(name = "transactionReference")
    private String transactionReference;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "customerId")
    private String customerId;

}
