package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepo extends JpaRepository<UserAccount, Integer> {
    boolean existsByEmail(String email);
    Optional<UserAccount> findByEmail(String email);
}