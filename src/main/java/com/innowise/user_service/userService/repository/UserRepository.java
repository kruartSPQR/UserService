package com.innowise.user_service.userService.repository;

import com.innowise.user_service.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    User getUserByEmail(@Param("email")  String email);

    @Query(value = "SELECT * FROM users WHERE user_id = :id", nativeQuery = true)
    User getUserById(@Param("id") Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEmail(String email);

}
