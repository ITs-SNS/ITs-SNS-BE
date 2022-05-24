package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.RecruitKeywordR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitKeywordRRepository extends JpaRepository<RecruitKeywordR, Long> {
}