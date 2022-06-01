package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.RecruitKeyword;
import com.aim.itssns.domain.entity.RecruitKeywordR;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecruitKeywordRRepository extends JpaRepository<RecruitKeywordR, Long> {
    List<RecruitKeywordR> findAllByRecruitKeyword(RecruitKeyword recruitKeyword, Pageable pageable);

    List<RecruitKeywordR> findAllByRecruitKeywordKeywordContentAndRecruitRecruitCrawlDate(String userKeywordContent, LocalDate date);
}