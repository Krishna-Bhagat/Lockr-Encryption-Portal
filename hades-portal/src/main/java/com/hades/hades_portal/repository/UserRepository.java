package com.hades.hades_portal.repository;

import com.hades.hades_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByPublicKey(String publicKey);

    boolean existsByUsername(String username);
}
