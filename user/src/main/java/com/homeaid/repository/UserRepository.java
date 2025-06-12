package com.homeaid.repository;

import com.homeaid.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhone(String phone);

    Optional<User> findByPhone(String phone);

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

}
