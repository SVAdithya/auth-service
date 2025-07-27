package com.app.auth.domain.repository;

import com.app.auth.domain.model.User;
import com.app.auth.domain.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    User save(User user);

    void deleteById(String id);

    boolean existsByEmail(String email);
}