package com.vexa.ecommerce.Users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndUserIdNot(String email, Integer userId);
    Optional<Users> findByEmail(String email);

}
