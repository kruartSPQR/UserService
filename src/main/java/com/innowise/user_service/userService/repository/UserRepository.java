package com.innowise.user_service.userService.repository;

import com.innowise.user_service.userService.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {

    @EntityGraph(attributePaths = "cardList")
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User getUserById(@Param("id") Long id);

    @EntityGraph(attributePaths = "cardList")
    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    User getUserByEmail(@Param("email")  String email);

    @Query(value = "SELECT * FROM users WHERE user_id = :id", nativeQuery = true)
    User getUserByIdNative(@Param("id") Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEmail(String email);

}
