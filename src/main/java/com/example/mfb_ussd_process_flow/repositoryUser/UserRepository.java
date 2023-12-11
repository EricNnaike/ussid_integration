package com.example.mfb_ussd_process_flow.repositoryUser;

import com.example.mfb_ussd_process_flow.entityUser.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Users> findUsersById(Long id);
}
