package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.Recruit;
import com.aim.itssns.domain.entity.RecruitKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitKeywordRepository extends JpaRepository<RecruitKeyword, Long> {
}