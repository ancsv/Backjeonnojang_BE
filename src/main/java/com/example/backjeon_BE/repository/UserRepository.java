package com.example.backjeon_BE.repository;

import com.example.backjeon_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName); // 사용자 이름으로 유저 검색

    Optional<User> findByEmail(String email); // 이메일로 유저 검색 (추가!)
}