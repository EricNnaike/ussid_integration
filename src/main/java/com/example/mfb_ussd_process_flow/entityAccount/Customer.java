package com.example.mfb_ussd_process_flow.entityAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_customer")
public class Customer {
    @Id
    @Column(name = "CustomerId")
    private String CustomerId;

    private String Phone1;
}
