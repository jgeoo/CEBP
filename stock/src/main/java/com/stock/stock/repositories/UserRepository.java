package com.stock.stock.repositories;

import com.stock.stock.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by username (added for login functionality)
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);

}
