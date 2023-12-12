package com.example.mfb_ussd_process_flow.repositoryUser;

import com.example.mfb_ussd_process_flow.entityUser.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
}
