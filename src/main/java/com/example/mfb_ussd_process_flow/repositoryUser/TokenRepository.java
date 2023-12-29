package com.example.mfb_ussd_process_flow.repositoryUser;

import com.example.mfb_ussd_process_flow.entityUser.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
        select t from Token t inner join Users u on t.users.id = u.id
        where u.id = :userId and (t.expired = false or t.revoked = false)
    """)
    List<Token> findAllValidTokensByUser(Long userId);

    @Query("""
        select t from Token t inner join Users u on t.users.id = u.id
        where u.id = :userId and (t.expired = true or t.revoked = true)
    """)
    List<Token> findAllExpiredTokensByUser(Long userId);
    Optional<Token> findByToken(String token);
    List<Token> findAllByExpiredAndRevoked(boolean expired, boolean revoked);
}
