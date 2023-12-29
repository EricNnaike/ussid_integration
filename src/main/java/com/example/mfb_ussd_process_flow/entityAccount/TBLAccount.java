package com.example.mfb_ussd_process_flow.entityAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_casaaccount")
public class TBLAccount {
    @Id
    @Column(name = "accountnumber")
    private String accountnumber;

    @Column(name = "customerid")
    private String customerid;

    @Column(name = "BKBalance")
    private BigDecimal BKBalance;

    @Column(name = "accounttitle")
    private String accounttitle;



}
