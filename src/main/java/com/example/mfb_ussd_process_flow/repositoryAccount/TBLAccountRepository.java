package com.example.mfb_ussd_process_flow.repositoryAccount;

import com.example.mfb_ussd_process_flow.entityAccount.TBLAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TBLAccountRepository extends JpaRepository<TBLAccount, String> {
    @Query("SELECT b.accountnumber FROM TBLAccount b JOIN Customer a ON a.CustomerId = b.customerid WHERE a.Phone1 = :phone1")
    Optional<String> findAccountNumberByPhone(@Param("phone1") String phone1);

    @Query("SELECT b FROM TBLAccount b JOIN Customer a ON a.CustomerId = b.customerid WHERE a.Phone1 = :phone1")
    Optional<TBLAccount> findTBLAccountByPhone(@Param("phone1") String phone1);

//    @Query("SELECT b FROM TBLAccount b JOIN Customer a ON a.CustomerId = b.customerid WHERE a.Phone1 = :phone1 OR b.accountNumber = :accountNumber")
//    Optional<TBLAccount> findTBLAccountByPhoneOrAccountNumber(@Param("phone1") String phone1, @Param("accountNumber") String accountNumber);
}
