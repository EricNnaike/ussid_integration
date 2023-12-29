package com.example.mfb_ussd_process_flow.repositoryAccount;

import com.example.mfb_ussd_process_flow.entityAccount.TBLAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TBLAccountRepository extends JpaRepository<TBLAccount, String> {
    @Query("SELECT b.accountnumber FROM TBLAccount b JOIN Customer a ON a.CustomerId = b.customerid WHERE a.Phone1 = :phone1")
    Optional<String> findAccountNumberByPhone(@Param("phone1") String phone1);

    @Query("SELECT b FROM TBLAccount b JOIN Customer a ON a.CustomerId = b.customerid WHERE a.Phone1 = :phone1")
    Optional<TBLAccount> findTBLAccountByPhone(@Param("phone1") String phone1);


//    @Query(" SELECT b.accountnumber\n" +
//            "    FROM TBLAccount b \n" +
//            "    WHERE b.customerid='SELECT b.customerid FROM Customer a WHERE a.Phone1 = :phone1'")
//    List<String> findAllByPhone(@Param("phone1") String phone1);

    @Query("SELECT b.accountnumber " +
            "FROM TBLAccount b " +
            "WHERE b.customerid IN (SELECT a.CustomerId FROM Customer a WHERE a.Phone1 = :phone1)")
    List<String> findAllByPhone(@Param("phone1") String phone1);

//    @Query("SELECT b FROM TBLAccount b JOIN Customer a ON a.CustomerId = b.customerid WHERE a.Phone1 = :phone1 OR b.accountNumber = :accountNumber")
//    Optional<TBLAccount> findTBLAccountByPhoneOrAccountNumber(@Param("phone1") String phone1, @Param("accountNumber") String accountNumber);
}
