package com.example.mfb_ussd_process_flow.repositoryUser;

import com.example.mfb_ussd_process_flow.entityUser.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNameIgnoreCase(String name);

}
