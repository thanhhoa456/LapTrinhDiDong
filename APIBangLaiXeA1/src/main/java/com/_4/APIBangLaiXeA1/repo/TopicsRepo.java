package com._4.APIBangLaiXeA1.repo;


import com._4.APIBangLaiXeA1.entity.Tests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicsRepo extends JpaRepository<Tests, Integer> {
}